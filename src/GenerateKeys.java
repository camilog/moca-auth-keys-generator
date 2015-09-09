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

    private static String bulletinBoardAddress = "";
    private static String authorityPublicKeySubDomain = "/authority_public_key";
    private static String dummyShareSubDomain = "/dummy_share";
    private static String user, pass;

    // Function which generate the public and private keys of the authorities, and uploads the public one
    protected static void generateKeys(int n, int k, SecureRandom r) throws IOException {
        /* Private Key Files */

        // Set output of generation of keys to ./out.log
        File f = new File("out.log");
        System.setOut(new PrintStream(f));

        // Generate keys with prime factor of n of 256 bits
        PaillierPrivateThresholdKey[] keys = KeyGen.PaillierThresholdKey(256, n + 1, k + 1, r.nextInt());

        // Delete ./out.log
        f.delete();

        // Recover standard output
        PrintStream ps = new PrintStream(new FileOutputStream(FileDescriptor.out));
        System.setOut(ps);

        uploadDummyShare(keys[0]);

        // Save in different files each authority key
        for (int i = 1; i < keys.length; i++) {
            savePrivateKeyToFile(i, keys[i]);
        }

        /* Public Key Value */

        // Upload public key to the BB (but before is necessary to make sure that the old key, if there's any, is deleted)
        deleteOldKey();
        uploadAndSavePublicKey(keys[0].getPublicKey().getN().toString(), k);

    }

    private static void uploadDummyShare(PaillierPrivateThresholdKey key) throws IOException {

        BigInteger[][] dummyShareBigIntegerArrays = getIndependentValues(key);
        String dummyShareJson = new Gson().toJson(new PrivateKey(dummyShareBigIntegerArrays));

        // TODO: Fix the values to upload to the BB

        // Set the URL where to POST the dummy share key
        URL obj = new URL(bulletinBoardAddress + dummyShareSubDomain);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // Add request header
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");

        // Create JSON with the parameters
        String dummyShare = getIndependentValues(key).toString();
        String urlParameters = "{\"value\":" + dummyShare + "}";

        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();
        con.getResponseCode();

    }

    // Function to save to a file the private keys as a BigInteger[][] with the independent values to create the same key at the other device
    public static void savePrivateKeyToFile(int authorityNumber, PaillierPrivateThresholdKey value) throws IOException {

        // Choose folder where to save the private key (external storage)
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Public Key");
        File folder = fileChooser.showSaveDialog(null);

        // Serialize private key as a Json
        String privateKeyJson = new Gson().toJson(new PrivateKey(getIndependentValues(value)));

        // Save JSON in a simple file
        PrintStream privateKeyOut = new PrintStream(folder.getPath() + "/" + authorityNumber + "_privateKey");
        privateKeyOut.println(privateKeyJson);

    }

    // Check if there's a uploaded public key already on the BB
    public static void deleteOldKey() throws IOException {

        // Set the URL to GET the public key of the authority
        URL obj = new URL(bulletinBoardAddress + authorityPublicKeySubDomain + "/_all_docs");
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

        String jsonString = response.toString();

        Gson gson = new Gson();
        AuthorityPublicKeyResponse authorityPublicKeyResponse = gson.fromJson(jsonString, AuthorityPublicKeyResponse.class);

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
    static private void uploadAndSavePublicKey(String publicKey, int threshold) throws IOException {

        // Choose folder where to save the public key
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Public Key");
        File folder = fileChooser.showSaveDialog(null);

        ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(folder.getPath() + "/publicKeyN")));
        oos.writeObject(new BigInteger(publicKey));
        oos.close();

        // Set the URL where to POST the public key
        URL obj = new URL(bulletinBoardAddress + authorityPublicKeySubDomain);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // Add request header
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");

        // Create JSON with the parameters
        String urlParameters = "{\"value\":" + publicKey + ",\"threshold\":" + threshold + "}";

        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();
        con.getResponseCode();
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

    // Function to set up the bulletin board address
    protected static void setBBAddress(String newAddress) {
        bulletinBoardAddress = newAddress;
    }

    // Function to retrieve the bulletin board address
    public static String getBBAddress() {
        return bulletinBoardAddress;
    }

}