package edu.pablorios.linktracker;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class LinkTracker extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(LinkTracker.class.getResource("FXMLMainView.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        stage.setScene(scene);
        stage.setTitle("LinkTracker");
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("miCss.css")).toExternalForm());
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}