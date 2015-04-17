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

        addComponent(new Button("Generate keys", new Action() {
            @Override
            public void doAction() {
                int n = Integer.parseInt(com.googlecode.lanterna.gui.dialog.TextInputDialog.showTextInputBox(getOwner(), "Parameters", "Number of Authorities", "", 4));
                int k = Integer.parseInt(com.googlecode.lanterna.gui.dialog.TextInputDialog.showTextInputBox(getOwner(), "Parameters", "Minimum Authorities", "", 4));
                SecureRandom r = new SecureRandom();

                try {
                    generateKeys(n, k, r);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                MessageBox.showMessageBox(getOwner(), "Finalizado", "Repartir valores publicos guardados en publicValues/\nRepartir partes de la clave privada entre las distintas autoridades, guardados en partsOfPrivateKey/\nProceso finalizado exitosamente.");
            }
        }));

        addComponent(new Button("Exit application", new Action() {
            @Override
            public void doAction() {
                // Salirse del window
                getOwner().getScreen().clear();
                getOwner().getScreen().refresh();
                getOwner().getScreen().setCursorPosition(0,0);
                getOwner().getScreen().refresh();
                getOwner().getScreen().stopScreen();
                System.exit(0);
            }
        }));

    }

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
        File dir2 = new File("partsOfPrivateKey");
        dir2.mkdir();
        PaillierPrivateThresholdKey[] keys = KeyGen.PaillierThresholdKey(256, n, k, r.nextInt());
        for (int i = 0; i < keys.length; i++){
            saveToFile("partsOfPrivateKey/privateKeyPart" + i + ".key", keys[i]);
        }

        // Public Key File
        File dir1 = new File("publicValue");
        dir1.mkdir();
        saveToFile("publicValue/publicKeyN.key", keys[0].getPublicKey().getN());
    }

    static public void main(String[] args) throws IOException {

        GenerateKeys myWindow = new GenerateKeys();
        GUIScreen guiScreen = TerminalFacade.createGUIScreen();
        Screen screen = guiScreen.getScreen();

        screen.startScreen();
        guiScreen.showWindow(myWindow, GUIScreen.Position.CENTER);
        screen.refresh();
        screen.stopScreen();

    }

}
