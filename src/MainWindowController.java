import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class MainWindowController {

    @FXML
    private Label bulletin_board_label;

    @FXML
    protected void handleConfigureBBAddressButtonAction(ActionEvent actionEvent) throws IOException {

        Parent root = FXMLLoader.load(getClass().getResource("configWindow.fxml"));
        Stage stage = new Stage();
        stage.setTitle("Configure Bulletin Board Address");
        stage.setScene(new Scene(root, 400, 200));
        stage.show();

    }

    @FXML
    protected void handleGenerateKeysButtonAction(ActionEvent actionEvent) throws IOException {

        Parent root = FXMLLoader.load(getClass().getResource("generationWindow.fxml"));
        Stage stage = new Stage();
        stage.setTitle("Generate Authority Keys");
        stage.setScene(new Scene(root, 700, 300));
        stage.show();

    }
}
