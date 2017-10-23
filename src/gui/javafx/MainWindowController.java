package gui.javafx;

import crypto.GenerateKeys;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class MainWindowController {

    @FXML
    protected TextField new_bulletin_board_address;

    @FXML
    protected Button change_bb_button;

    @FXML
    protected Label bulletin_board_label;

    @FXML
    public void handleDisplayChangeBBAddressButtonAction(ActionEvent actionEvent) throws IOException {

        new_bulletin_board_address.setVisible(true);
        change_bb_button.setVisible(true);

    }

    @FXML
    public void handleChangeBBAddressButtonAction(ActionEvent actionEvent) {

        String newBBAddress = new_bulletin_board_address.getText();

        GenerateKeys.setBBAddress(newBBAddress);

        bulletin_board_label.setText(GenerateKeys.getBBAddress());
        change_bb_button.setVisible(false);
        new_bulletin_board_address.setVisible(false);

    }

    @FXML
    protected void handleGenerateKeysButtonAction(ActionEvent actionEvent) throws IOException {

        Parent root = FXMLLoader.load(getClass().getResource("gui/javafx/generationWindow.fxml"));
        Stage stage = new Stage();
        stage.setTitle("Generate Authority Keys");
        stage.setScene(new Scene(root, 700, 300));
        stage.show();

    }

}
