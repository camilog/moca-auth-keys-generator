package com.threepartballot;

import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.gui.Action;
import com.googlecode.lanterna.gui.GUIScreen;
import com.googlecode.lanterna.gui.Window;
import com.googlecode.lanterna.gui.component.Button;
import com.googlecode.lanterna.gui.dialog.MessageBox;
import com.googlecode.lanterna.screen.Screen;

import paillierp.key.KeyGen;
import paillierp.key.PaillierKey;
import paillierp.key.PaillierPrivateThresholdKey;

import java.io.*;
import java.math.BigInteger;
import java.security.SecureRandom;

public class GenerateKeys extends Window {

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
    public static void saveToFile(String fileName, PaillierKey value) throws IOException {
        ObjectOutputStream oout = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(fileName)));
        try{
            oout.writeObject(value);
        } catch (Exception e) {
            throw new IOException("Unexpected error", e);
        } finally {
            oout.close();
        }
    }

    // Function to save to a file the public key
    public static void saveToFile(String fileName, BigInteger value) throws IOException {
        ObjectOutputStream oout = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(fileName)));
        try{
            oout.writeObject(value);
        } catch (Exception e) {
            throw new IOException("Unexpected error", e);
        } finally {
            oout.close();
        }
    }

    public static void generateKeys(int n, int k, SecureRandom r) throws IOException {
        // Private Key Files.
        // Create Directory if its not created
        File dir1 = new File("partsOfPrivateKey");
        dir1.mkdir();

        // Delete all previous keys
        for (File f : dir1.listFiles())
            f.delete();

        System.setOut(new PrintStream(new File("out.log"))); // Set output of generation of keys to ./out.log
        PaillierPrivateThresholdKey[] keys = KeyGen.PaillierThresholdKey(256, n, k, r.nextInt()); // Generate keys with prime factor of n of 256 bits
        System.setOut(System.out); // Recover standard output

        for (int i = 0; i < keys.length; i++){
            saveToFile("partsOfPrivateKey/privateKeyPart" + i + ".key", keys[i]); // Save in different files each authority key
        }

        // Public Key File
        // Create Directory if its not created
        File dir2 = new File("publicValue");
        dir2.mkdir();

        // Delete all previous keys
        for (File f : dir2.listFiles())
            f.delete();

        saveToFile("publicValue/publicKeyN.key", keys[0].getPublicKey().getN()); // Save in a file the PublicKey
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
