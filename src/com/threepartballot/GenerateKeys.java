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
import java.net.URL;
import java.net.URLConnection;
import java.security.SecureRandom;

public class GenerateKeys extends Window {

    private static final String ftpServer = "cjgomez.duckdns.org";
    private static final String user = "pi";
    private static final String pass = "CamiloGomez";

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

    // Function to save to a file the public key, and upload it to the BB
    public static void saveToFileAndUpload(String fileName, BigInteger value) throws IOException {
        ObjectOutputStream oout = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(fileName)));
        try{
            oout.writeObject(value);
        } catch (Exception e) {
            throw new IOException("Unexpected error", e);
        } finally {
            oout.close();
        }

        File publicKeyFile = new File(fileName);
        upload(ftpServer, user, pass, "publicInformation/authorityPublicKey", publicKeyFile);

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

        saveToFileAndUpload("publicValue/publicKeyN.key", keys[0].getPublicKey().getN()); // Save in a file the PublicKey
    }

    // Upload of the file
    /**
     * Upload a file to a FTP server. A FTP URL is generated with the
     * following syntax:
     * ftp://user:password@host:port/filePath;type=i.
     *
     * @param ftpServer , FTP server address (optional port ':portNumber').
     * @param user , Optional user name to login.
     * @param password , Optional password for user.
     * @param fileName , Destination file name on FTP server (with optional
     *            preceding relative path, e.g. "myDir/myFile.txt").
     * @param source , Source file to upload.
     * @throws IOException on error.
     */
    public static void upload( String ftpServer, String user, String password,
                               String fileName, File source ) throws IOException
    {
        if (ftpServer != null && fileName != null && source != null)
        {
            StringBuffer sb = new StringBuffer( "ftp://" );
            // check for authentication else assume its anonymous access.
            if (user != null && password != null)
            {
                sb.append( user );
                sb.append( ':' );
                sb.append( password );
                sb.append( '@' );
            }
            sb.append( ftpServer );
            sb.append( '/' );
            sb.append( fileName );
         /*
          * type ==&gt; a=ASCII mode, i=image (binary) mode, d= file directory
          * listing
          */
            sb.append( ";type=i" );

            BufferedInputStream bis = null;
            BufferedOutputStream bos = null;
            try
            {
                URL url = new URL( sb.toString() );
                URLConnection urlc = url.openConnection();

                bos = new BufferedOutputStream( urlc.getOutputStream() );
                bis = new BufferedInputStream( new FileInputStream( source ) );

                int i;
                // read byte by byte until end of stream
                while ((i = bis.read()) != -1)
                {
                    bos.write( i );
                }
            }
            finally
            {
                if (bis != null)
                    try
                    {
                        bis.close();
                    }
                    catch (IOException ioe)
                    {
                        ioe.printStackTrace();
                    }
                if (bos != null)
                    try
                    {
                        bos.close();
                    }
                    catch (IOException ioe)
                    {
                        ioe.printStackTrace();
                    }
            }
        }
        else
        {
            System.out.println( "Input not available." );
        }
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
