import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class GUIJavaFXController {

    @FXML private Label bulletin_board_label;
    @FXML private TextField new_bb_address_textfield;

    public void handleConfigureBBAddressButtonAction(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("configureBB.fxml"));
        Stage stage = new Stage();
        stage.setTitle("Configure Bulletin Board Address");
        stage.setScene(new Scene(root, 400, 200));
        stage.show();

        // hide this current window (if this is whant you want
        // ((Node)(actionEvent.getSource())).getScene().getWindow().hide();

    }

    public void handleGenerateKeysButtonAction(ActionEvent actionEvent) throws IOException {

        Parent root = FXMLLoader.load(getClass().getResource("GUIFX/generateKeys.fxml"));
        Stage stage = new Stage();
        stage.setTitle("Generate Authority Keys");
        stage.setScene(new Scene(root, 400, 200));
        stage.show();

    }

    @FXML public void handleNewBBAddressButtonAction(ActionEvent actionEvent) {

        System.out.println("You clicked me!");
        actionEvent.getSource()


    }
}
