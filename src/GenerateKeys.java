import com.google.gson.Gson;
import javafx.stage.FileChooser;
import paillierp.key.KeyGen;
import paillierp.key.PaillierPrivateThresholdKey;

import java.io.*;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;

public class GenerateKeys {

    private static String bulletinBoardAddress = "http://0.0.0.0:5000";
    private static String authorityPublicKeySubDomain = "/api/auth_public_key";
    private static String dummyShareSubDomain = "/api/dummy_share_key";
    private static String candidatesListSubDomain = "/api/candidates_list";

    // Function which generate the public and private keys of the authorities, and uploads the public one
    protected static void generateKeys(int n, int k, SecureRandom r) throws IOException {
        // Set output of generation of keys to ./out.log
        File f = new File("out.log");
        System.setOut(new PrintStream(f));

        // Generate keys with prime factor of n of 256 bits. Threshold scheme (n,k) plus dummy share.
        PaillierPrivateThresholdKey[] keys = KeyGen.PaillierThresholdKey(256, n + 1, k + 1, r.nextInt());

        // Delete ./out.log and recover standard output
        f.delete();
        PrintStream ps = new PrintStream(new FileOutputStream(FileDescriptor.out));
        System.setOut(ps);

        // Save in different files each authority key
        for (int i = 1; i < keys.length; i++) {
            savePrivateKeyToFile(i, keys[i]);
        }

        // Upload dummy share of the private key in order to combine the partial decryptions (delete the previous one)
//        deleteOldDummyShare();
        uploadDummyShare(keys[0]);

        // Upload to the BB and save locally the public key (but before is necessary to make sure that the old key, if there's any, is deleted)
//        deleteOldKey();
        uploadAndSavePublicKey(keys[0].getPublicKey().getN().toString(), keys[0].getPublicKey().getNSPlusOne().toString(), k);
//
        downloadAndSaveCandidatesList();

    }

    // TODO
    private static void deleteOldDummyShare() {
    }

    // TODO: Refactor de la labor de subir/bajar documentos al BB
    private static void downloadAndSaveCandidatesList() throws IOException {

//        String id = getIdOfTheUploadedParameter(bulletinBoardAddress + candidatesListSubDomain).rows[0].id;

        // Set the URL to GET the public key of the authority
        URL obj = new URL(bulletinBoardAddress + candidatesListSubDomain);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // Add request header
        con.setRequestMethod("GET");
        con.setRequestProperty("Content-Type", "application/json");
        con.getResponseCode();

        // Receive the response
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null)
            response.append(inputLine);
        in.close();

        // Serialize the JSON response to an Object (AuthorityPublicKeyResponse)
        String jsonString = response.toString();

        // Choose folder where to save the candidates list
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Candidates List");
        fileChooser.setInitialFileName("candidatesList.json");
        File pathFile = fileChooser.showSaveDialog(null);

        // Save JSON in a simple file
        PrintStream privateKeyOut = new PrintStream(pathFile);
        privateKeyOut.println(jsonString);

    }

    private static GenericUniqueParameterResponse getIdOfTheUploadedParameter(String parameterSubDomainURL) throws IOException {

        // Set the URL to GET the public key of the authority
        URL obj = new URL(parameterSubDomainURL + "/_all_docs");
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // Add request header
        con.setRequestMethod("GET");
        con.setRequestProperty("Content-Type", "application/json");
        con.getResponseCode();

        // Receive the response
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null)
            response.append(inputLine);
        in.close();

        // Serialize the JSON response to an Object (AuthorityPublicKeyResponse)
        String jsonString = response.toString();
        Gson gson = new Gson();
        return gson.fromJson(jsonString, GenericUniqueParameterResponse.class);

    }

    private static void uploadDummyShare(PaillierPrivateThresholdKey key) throws IOException {

        // Get the values of the dummy share in order to upload them to the BB
        BigInteger[][] dummyShareBigIntegerArrays = getIndependentValues(key);

        // Set the URL where to POST the dummy share key
        URL obj = new URL(bulletinBoardAddress + dummyShareSubDomain);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // Add request header
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");

        // Create JSON with the parameters
        String urlParameters = new Gson().toJson(new PrivateKey(dummyShareBigIntegerArrays));

        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();
        con.getResponseCode();

    }

    // Function to save to a file the private keys as a JSON representing a BigInteger[][] with the independent values to create the same key at the other device
    public static void savePrivateKeyToFile(int authorityNumber, PaillierPrivateThresholdKey value) throws IOException {

        // Choose folder where to save the private key (external storage)
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Private Key #" + authorityNumber);
        fileChooser.setInitialFileName(authorityNumber + "_privateKey");
        File pathFile = fileChooser.showSaveDialog(null);

        // Serialize private key as a Json
        String privateKeyJson = new Gson().toJson(new PrivateKey(getIndependentValues(value)));

        // Save JSON in a simple file
        PrintStream privateKeyOut = new PrintStream(pathFile);
        privateKeyOut.println(privateKeyJson);

    }

    // Check if there's a uploaded public key already on the BB
    public static void deleteOldKey() throws IOException {

        // Set the URL to GET the public key of the authority
        URL obj = new URL(bulletinBoardAddress + authorityPublicKeySubDomain);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // Add request header
        con.setRequestMethod("GET");
        con.setRequestProperty("Content-Type", "application/json");
        con.getResponseCode();

        // Receive the response
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null)
            response.append(inputLine);
        in.close();

        // Serialize the JSON response to an Object (AuthorityPublicKeyResponse)
        String jsonString = response.toString();
        Gson gson = new Gson();
        AuthorityPublicKeyResponse authorityPublicKeyResponse = gson.fromJson(jsonString, AuthorityPublicKeyResponse.class);

        // Check if there's already a key on the BB, if so, delete it
        if (authorityPublicKeyResponse.total_rows > 0) {

            // Set the URL to DELETE the public key of the authority
            obj = new URL(bulletinBoardAddress + authorityPublicKeySubDomain + "/" + authorityPublicKeyResponse.rows[0].id + "?rev=" + authorityPublicKeyResponse.rows[0].value.rev);
            con = (HttpURLConnection) obj.openConnection();

            // Add request header
            con.setRequestMethod("DELETE");
            con.setRequestProperty("Content-Type", "application/json");
            con.getResponseCode();

        }

    }

    // Upload of the publicKey as a JSON to the bbServer. Also is being stored locally (to give it to the tablet or non-connected to Internet devices).
    static private void uploadAndSavePublicKey(String publicKeyN, String publicKeyNSPlusOne, int threshold) throws IOException {

        // Choose folder where to save the public key
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Public Key");
        fileChooser.setInitialFileName("publicKeyN");
        File pathFile = fileChooser.showSaveDialog(null);

        // Save locally the public key in a file called '/publicKeyN'
        ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(pathFile)));
        oos.writeObject(new BigInteger(publicKeyN));
        oos.close();

        // Set the URL where to POST the public key
        URL obj = new URL(bulletinBoardAddress + authorityPublicKeySubDomain);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // Add request header
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");

        // Create JSON with the parameters
        String urlParameters = "{\"n\":" + publicKeyN + ",\"threshold\":" + threshold + ",\"nsplusone\":" + publicKeyNSPlusOne + "}";

        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();
        con.getResponseCode();
    }

    // Function to retrieve the values that make a private key, in order to serialize it and save it into a file
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

    // Function to set up the bulletin board address
    protected static void setBBAddress(String newAddress) {
        bulletinBoardAddress = newAddress;
    }

    // Function to retrieve the bulletin board address
    public static String getBBAddress() {
        return bulletinBoardAddress;
    }

}