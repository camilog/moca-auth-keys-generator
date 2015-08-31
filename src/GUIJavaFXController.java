import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class GUIJavaFXController {

    public void handleConfigureBBAddressButtonAction(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("GUIFX/configureBB.fxml"));
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

    public void handleNewBBAddressButtonAction(ActionEvent actionEvent) {

    }
}
