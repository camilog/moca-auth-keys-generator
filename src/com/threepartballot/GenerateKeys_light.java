package com.threepartballot;

import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.gui.Action;
import com.googlecode.lanterna.gui.GUIScreen;
import com.googlecode.lanterna.gui.Window;
import com.googlecode.lanterna.gui.component.Button;
import com.googlecode.lanterna.gui.dialog.MessageBox;
import com.googlecode.lanterna.screen.Screen;

import java.io.*;
import java.security.SecureRandom;

public class GenerateKeys_light extends Window {


    public GenerateKeys_light() {
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

                // Generate Keys with the different parameters
                try {
                    // generateKeys(n, k, r);
                    GenerateKeys_CORE.generateKeys(n, k, r);
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

    static public void main(String[] args) throws IOException {

        // Create window to display options
        GenerateKeys_light myWindow = new GenerateKeys_light();
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
