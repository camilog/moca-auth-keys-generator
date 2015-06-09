package com.threepartballot;

import com.google.gson.Gson;
import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.gui.Action;
import com.googlecode.lanterna.gui.GUIScreen;
import com.googlecode.lanterna.gui.Window;
import com.googlecode.lanterna.gui.component.Button;
import com.googlecode.lanterna.gui.dialog.FileDialog;
import com.googlecode.lanterna.gui.dialog.MessageBox;
import com.googlecode.lanterna.screen.Screen;

import paillierp.key.KeyGen;
import paillierp.key.PaillierKey;
import paillierp.key.PaillierPrivateThresholdKey;

import java.io.*;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.SecureRandom;

import javax.swing.*;

public class GenerateKeys extends Window {

    private static final String authPublicKeyServer = "http://cjgomez.duckdns.org:3000/authority_public_keys";

    public GenerateKeys() {
        super("Generate Authority Keys");

        // Add button to generate keys
        addComponent(new Button("Generate keys", new Action() {
            @Override
            public void doAction() {
                // Retrieve number of authorities to share the private key
                int n = Integer.parseInt(com.googlecode.lanterna.gui.dialog.TextInputDialog.showTextInputBox(getOwner(), "Parameters", "Number of Authorities", "", 4));

                // Retrieve the minimum of authorities needed to perform decryption
                int k = Integer.parseInt(com.googlecode.lanterna.gui.dialog.TextInputDialog.showTextInputBox(getOwner(), "Parameters", "Minimum Authorities", "", 4));

                // Generate secure random number
                SecureRandom r = new SecureRandom();

                try {
                    // Generate Keys with the different parameters
                    generateKeys(n, k, r);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // Final message in case of success
                MessageBox.showMessageBox(getOwner(), "Finalizado", "Repartir valores publicos guardados en publicValues/\nRepartir partes de la clave privada entre las distintas autoridades, guardados en partsOfPrivateKey/\nProceso finalizado exitosamente.");
            }
        }));

        // Add button to finalize application
        addComponent(new Button("Exit application", new Action() {
            @Override
            public void doAction() {
                // Close window properly and finalize application
                getOwner().getScreen().clear();
                getOwner().getScreen().refresh();
                getOwner().getScreen().setCursorPosition(0, 0);
                getOwner().getScreen().refresh();
                getOwner().getScreen().stopScreen();
                System.exit(0);
            }
        }));

    }

    // Function to save to a file the private keys
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

    /*
    public static void saveToFile(int authorityNumber, PaillierPrivateThresholdKey value) throws IOException {
        // Open dialog to choose the folder where to store the private keys of the authorities
        JFileChooser f = new JFileChooser();
        f.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        f.showSaveDialog(null);

        // Retrieve the value of the private key as a String to store it in the file
        // TODO: Analizar si esta manera de guardar el archivo es la correcta, pensando en que la función .toByteArray() está teniendo problemas con la librería tal como viene
        String valueString = new BigInteger(value.toByteArray()).toString();

        // Create the file where to store the private key
        File valueFile = new File(f.getSelectedFile(), authorityNumber + "_privateKey");
        valueFile.createNewFile();

        // Write the value of the public key in the file (if the value will be stored as a String)
        BufferedWriter writer = new BufferedWriter(new FileWriter(valueFile, true));
        writer.write(valueString);
        writer.close();
    }
    */

    // Upload of the publicKey as a JSON to the bbServer
    static private void upload(String authPublicKeyServer, String publicKey) throws IOException {
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

    public static void generateKeys(int n, int k, SecureRandom r) throws IOException {
        // Private Key Files.
        File f = new File("out.log");
        System.setOut(new PrintStream(f)); // Set output of generation of keys to ./out.log

        // Generate keys with prime factor of n of 256 bits
        PaillierPrivateThresholdKey[] keys = KeyGen.PaillierThresholdKey(256, n, k, r.nextInt());

        f.delete(); // Delete ./out.log

        // Recover standard output
        PrintStream ps = new PrintStream(new FileOutputStream(FileDescriptor.out));
        System.setOut(ps);

        // Save in different files each authority key
        for (int i = 0; i < keys.length; i++){
            saveToFile(i, keys[i]);
        }

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
        StringBuffer response = new StringBuffer();
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

    static public void main(String[] args) throws IOException {

        // Create window to display options
        GenerateKeys myWindow = new GenerateKeys();
        GUIScreen guiScreen = TerminalFacade.createGUIScreen();
        Screen screen = guiScreen.getScreen();

        // Start and configuration of the screen
        screen.startScreen();
        guiScreen.showWindow(myWindow, GUIScreen.Position.CENTER);
        screen.refresh();

        // Stopping screen at finalize application
        screen.stopScreen();

    }

}
