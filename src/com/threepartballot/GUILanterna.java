package com.threepartballot;

import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.gui.GUIScreen;
import com.googlecode.lanterna.gui.Window;
import com.googlecode.lanterna.gui.component.Button;
import com.googlecode.lanterna.gui.component.Label;
import com.googlecode.lanterna.gui.component.Panel;
import com.googlecode.lanterna.gui.dialog.MessageBox;
import com.googlecode.lanterna.gui.dialog.TextInputDialog;
import com.googlecode.lanterna.screen.Screen;

import java.io.*;
import java.security.SecureRandom;

public class GUILanterna extends Window {

    public GUILanterna() {
        super("Generate Authority Keys");

        // Panel with the current bulletin board address
        // TODO: Poner este label en una esquina (reordenar paneles de toda la pantalla)
        Panel addressPanel = new Panel("Current Bulletin Board address");
        addressPanel.addComponent(new Label());

        // Add Bulletin-Board-address panel
        addComponent(addressPanel);
        updateAddressLabel((Label) addressPanel.getComponentAt(0));

        // Add button to set up BB address
        addComponent(new Button("Configure Bulletin Board address", () -> {
            // Retrieve string of the bulletin board address
            String newAddress = TextInputDialog.showTextInputBox(getOwner(),
                    "Bulletin Board address", "New Bulletin Board address", "", 20);

            // Set new bulletin board address and update label showing it
            GenerateKeys.setBBAddress(newAddress);
            updateAddressLabel((Label) addressPanel.getComponentAt(0));

            // Final message in case of success
            MessageBox.showMessageBox(getOwner(),
                    "Finalizado", "Nueva dirección del Bulletin Board exitosamente guardada.");
        }));

        // Add button to generate keys
        addComponent(new Button("Generate keys", () -> {
            // Retrieve number of authorities to share the private key
            int n = Integer.parseInt(com.googlecode.lanterna.gui.dialog.TextInputDialog.showTextInputBox(getOwner(), "Parameters", "Number of Authorities", "", 4));

            // Retrieve the minimum of authorities needed to perform decryption
            int k = Integer.parseInt(com.googlecode.lanterna.gui.dialog.TextInputDialog.showTextInputBox(getOwner(), "Parameters", "Minimum Authorities", "", 4));

            // Generate secure random number
            SecureRandom r = new SecureRandom();

            // Generate Keys with the different parameters
            try {
                GenerateKeys.generateKeys(n, k, r, 0);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Final message in case of success
            MessageBox.showMessageBox(getOwner(), "Finalizado", "Repartir valores publicos guardados en publicValues/\nRepartir partes de la clave privada entre las distintas autoridades, guardados en partsOfPrivateKey/\nProceso finalizado exitosamente.");
        }));

        // Add button to finalize application
        addComponent(new Button("Exit application", () -> {
            // Close window properly and finalize application
            getOwner().getScreen().clear();
            getOwner().getScreen().refresh();
            getOwner().getScreen().setCursorPosition(0, 0);
            getOwner().getScreen().refresh();
            getOwner().getScreen().stopScreen();
            System.exit(0);
        }));

    }

    // Update the text shown in the current bulletin board address
    static private void updateAddressLabel(Label label) {
        label.setText(GenerateKeys.getBBAddress());
    }

    static public void main(String[] args) throws IOException {

        // Create window to display options
        GUILanterna myWindow = new GUILanterna();
        GUIScreen guiScreen = TerminalFacade.createGUIScreen();
        GenerateKeys.setGuiScreen(guiScreen);
        Screen screen = guiScreen.getScreen();

        // Start and configuration of the screen
        screen.startScreen();
        guiScreen.showWindow(myWindow, GUIScreen.Position.CENTER);
        screen.refresh();

        // Stopping screen at finalize application
        screen.stopScreen();

    }

}
