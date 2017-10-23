package gui.javafx;

import crypto.GenerateKeys;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.security.SecureRandom;

public class GenerationWindowController {

    @FXML
    private TextField authorities_number, minimum_number;

    @FXML
    public void handleParametersReadyButtonAction(ActionEvent actionEvent) throws IOException {

        int n = Integer.parseInt(authorities_number.getText());
        int k = Integer.parseInt(minimum_number.getText());
        GenerateKeys.generateKeys(n, k, new SecureRandom());

        // Close window
        ((Node) (actionEvent.getSource())).getScene().getWindow().hide();

    }

}
