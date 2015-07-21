package com.threepartballot;

import com.google.gson.Gson;
import paillierp.ByteUtils;
import paillierp.key.KeyGen;
import paillierp.key.PaillierPrivateThresholdKey;

import javax.swing.*;
import java.io.*;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;

public class GenerateKeys_CORE {

    private static final String authPublicKeyServer = "http://cjgomez.duckdns.org:3000/authority_public_keys";

    // Function to save to a file the private keys as a serialized PaillierKey
    /*
    public static void saveToFile(int authorityNumber, PaillierKey value) throws IOException {
        // Chooser of the folder to save the private keys
        // TODO: Cambiar esto a que funcione en ambiente Lanterna
        JFileChooser f = new JFileChooser();
        f.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        f.showSaveDialog(null);

        ObjectOutputStream oout = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(f.getSelectedFile() + "/" + authorityNumber + "_privateKey.key")));
        try{
            oout.writeObject(value);
        } catch (Exception e) {
            throw new IOException("Unexpected error", e);
        } finally {
            oout.close();
        }
    }
    */

    /*
    // Function to save to a file the private keys as a String
    public static void saveToFile(int authorityNumber, PaillierPrivateThresholdKey value) throws IOException {
        // Open dialog to choose the folder where to store the private keys of the authorities
        JFileChooser f = new JFileChooser();
        f.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        f.showSaveDialog(null);

        // Retrieve the value of the private key as a String to store it in the file
        // TODO: Analizar si esta manera de guardar el archivo es la correcta, pensando en que la función .toByteArray() está teniendo problemas con la librería tal como viene
        String valueString = new BigInteger(myToByteArray(value)).toString();

        // Create the file where to store the private key
        File valueFile = new File(f.getSelectedFile(), authorityNumber + "_privateKey");
        valueFile.createNewFile();

        // Write the value of the public key in the file (if the value will be stored as a String)
        BufferedWriter writer = new BufferedWriter(new FileWriter(valueFile, true));
        writer.write(valueString);
        writer.close();
    }
    */

    // Function to save to a file the private keys as a BigInteger[][] with the independent values to create the same key at the other device
    public static void saveToFile(int authorityNumber, PaillierPrivateThresholdKey value) throws IOException {
        // Open dialog to choose the folder where to store the private keys of the authorities
        JFileChooser f = new JFileChooser();
        f.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        f.showSaveDialog(null);

        ObjectOutputStream oout = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(f.getSelectedFile() + "/" + authorityNumber + "_privateKey")));
        oout.writeObject(getIndependentValues(value));
        oout.close();
    }


    // Upload of the publicKey as a JSON to the bbServer
    static private void upload(String authPublicKeyServer, String publicKey) throws IOException {
        // We are going also to save in a file the public Key (to give it to the tablet or non-connected to Internet devices)
        // Open dialog to choose the folder where to store the private keys of the authorities
        JFileChooser f = new JFileChooser();
        f.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        f.showSaveDialog(null);

        ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(f.getSelectedFile() + "/publicKeyN")));
        oos.writeObject(new BigInteger(publicKey));
        oos.close();

        /*File publicKeyFile = new File(f.getSelectedFile() + "/publicKeyN");
        publicKeyFile.createNewFile();
        BufferedWriter writer = new BufferedWriter(new FileWriter(publicKeyFile, true));
        writer.write(publicKey);
        writer.close();
*/
        // Set the URL where to POST the public key
        URL obj = new URL(authPublicKeyServer);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // Add request header
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");

        // Create JSON with the parameters
        String urlParameters = "{\"authority_public_key\":{\"key\":" + publicKey + "}}";

        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();
        con.getResponseCode();
    }

    // Function which generate the public and private keys of the authorities, and uploads the public one
    protected static void generateKeys(int n, int k, SecureRandom r) throws IOException {
        // Private Key Files.
        // Set output of generation of keys to ./out.log
        File f = new File("out.log");
        System.setOut(new PrintStream(f));

        // Generate keys with prime factor of n of 256 bits
        PaillierPrivateThresholdKey[] keys = KeyGen.PaillierThresholdKey(256, n, k, r.nextInt());

        // Delete ./out.log
        f.delete();

        // Recover standard output
        PrintStream ps = new PrintStream(new FileOutputStream(FileDescriptor.out));
        System.setOut(ps);

        // Save in different files each authority key
        for (int i = 0; i < keys.length; i++){
            saveToFile(i, keys[i]);
        }

        // Public Key Value
        // Upload public key to the BB (but before is necessary to make sure that the old key, if there's any, is deleted)
        int id;
        if ((id = uploadedKey()) > 0)
            deleteOldKey(id);
        upload(authPublicKeyServer, keys[0].getPublicKey().getN().toString());

    }

    private static void deleteOldKey(int id) throws IOException {
        // Set the URL to DELETE the public key of the authority
        URL obj = new URL(authPublicKeyServer + "/" + id);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // Add request header
        con.setRequestMethod("DELETE");
        con.setRequestProperty("Content-Type", "application/json");
        con.getResponseCode();

    }

    public static int uploadedKey() throws IOException {
        // Set the URL to GET the public key of the authority
        URL obj = new URL(authPublicKeyServer);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // Add request header
        con.setRequestMethod("GET");
        con.setRequestProperty("Content-Type", "application/json");
        con.getResponseCode();

        // Receive the response
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        String jsonString = response.toString();
        Gson gson = new Gson();
        AuthorityPublicKey[] authPublicKey = gson.fromJson(jsonString, AuthorityPublicKey[].class);

        if (authPublicKey.length > 0)
            return authPublicKey[0].id;

        return 0;
    }


    static private byte[] myToByteArray(PaillierPrivateThresholdKey key) {
        byte[] result;

        // PaillierKey modified .toByteArray()
        int size = key.getN().toByteArray().length;
        byte[] r = new byte[size];
        System.arraycopy(key.getN().toByteArray(), 0, r, 0, size); // This line has been modified
        result = r;

        // PaillierThresholdKey exact same .toByteArray()
        r = ByteUtils.appendInt(result, key.getL(), key.getW());
        if (r.length == 0) {result = r;}
        r = ByteUtils.appendBigInt(result, key.getV());
        if (r.length == 0) {result = r;}
        r = ByteUtils.appendBigInt(result, key.getVi());
        if (r.length == 0) {result = r;}
        r = ByteUtils.appendInt(r, result.length);
        result = r;

        // PaillierPrivateThresholdKey exact same .toByteArray();
        r = ByteUtils.appendInt(result, key.getID());
        r = ByteUtils.appendBigInt(r, key.getSi());
        r = ByteUtils.appendInt(r, result.length);
        result = r;

        return result;
    }

    static private BigInteger[][] getIndependentValues(PaillierPrivateThresholdKey value) {
        BigInteger n = value.getN();
        BigInteger l = new BigInteger(String.valueOf(value.getL()));
        BigInteger w = new BigInteger(String.valueOf(value.getW()));
        BigInteger v = value.getV();
        BigInteger[] vi = value.getVi();
        BigInteger si = value.getSi();
        BigInteger i = new BigInteger(String.valueOf(value.getID()));

        BigInteger[] numbers = {n, l, w, v, si, i};
        BigInteger[][] result = new BigInteger[2][1];

        result[0] = numbers;
        result[1] = vi;

        return result;

    }

}
