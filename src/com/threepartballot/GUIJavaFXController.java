package com.threepartballot;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class GUIJavaFXController {

    public void handleConfigureBBAction(ActionEvent actionEvent) throws IOException {
        Parent root;

        root = FXMLLoader.load(getClass().getResource("configureBBAddress.fxml"));

        Stage stage = new Stage();
        stage.setTitle("Configure Bulletin Board Address");
        stage.setScene(new Scene(root, 250, 250));
        stage.show();

    }
}
