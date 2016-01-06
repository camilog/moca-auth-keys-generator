package com.threepartballot;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class GUIJavaFX extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("guifx.fxml"));

        Scene scene = new Scene(root, 500, 400);

        primaryStage.setTitle("Authority Keys Generator");
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    static public void main(String[] args) {
        launch(args);
    }
}