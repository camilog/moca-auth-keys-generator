/*
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.security.SecureRandom;

public class GUISwing extends JFrame {

    public GUISwing() {
        initComponents();
    }

    JLabel bulletinBoardAddressLabel = new JLabel("Current Bulletin Board Address: ");

    private void initComponents() {

        setTitle("Authority Keys Generator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JButton configurationButton = new JButton("Configure Bulletin Board address");
        JButton generateKeysButton = new JButton("Generate Authority Keys");

        configurationButton.addActionListener(e -> showConfigurationWindow());

        generateKeysButton.addActionListener(e -> showKeysGenerationWindow());

        setLayout(new BoxLayout(this.getContentPane(), BoxLayout.PAGE_AXIS));
        setSize(500, 200);
        setLocationRelativeTo(null);
        add(bulletinBoardAddressLabel);
        add(configurationButton);
        add(generateKeysButton);

    }

    private void showConfigurationWindow() {
        final JFrame frame = new JFrame("Configure Bulletin Board address");

        JLabel addressLabel = new JLabel("Ingrese dirección del Bulletin Board");
        final JTextField addressTextField = new JTextField();

        JButton okButton = new JButton("Ok");
        okButton.addActionListener(e -> {
            String newAddress = addressTextField.getText();
            crypto.GenerateKeys.setBBAddress(newAddress);
            uploadBulletinBoardAddressLabel();
            frame.setVisible(false);
        });

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(1,2));
        panel.add(addressLabel);
        panel.add(addressTextField);

        frame.setLayout(new GridLayout(2, 1));
        frame.add(panel);
        frame.add(okButton);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

    }

    private void uploadBulletinBoardAddressLabel() {
        bulletinBoardAddressLabel.setText("Current Bulletin Board Address: " + crypto.GenerateKeys.getBBAddress());
    }

    private void showKeysGenerationWindow() {
        JFrame frame = new JFrame("Generate Authority Keys");

        JLabel nLabel = new JLabel("Ingrese N° de autoridades");
        JTextField nTextField = new JTextField();

        JLabel kLabel = new JLabel("Ingrese mínimo de autoridades necesarias");
        JTextField kTextField = new JTextField();

        JButton okButton = new JButton("Ok");
        okButton.addActionListener(e -> {
            int n = Integer.parseInt(nTextField.getText());
            int k = Integer.parseInt(kTextField.getText());

            try {
                crypto.GenerateKeys.generateKeys(n, k, new SecureRandom(), 1);
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            // TODO: Avisar que las claves fueron creadas

            frame.setVisible(false);
        });

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2,4));
        panel.add(nLabel);
        panel.add(nTextField);
        panel.add(kLabel);
        panel.add(kTextField);

        frame.setLayout(new GridLayout(2, 1));
        frame.add(panel);
        frame.add(okButton);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

    }

    public static void main(String[] args) {

        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        EventQueue.invokeLater(() -> new GUISwing().setVisible(true));

    }

}
*/
