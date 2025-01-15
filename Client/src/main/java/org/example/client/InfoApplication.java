package org.example.client;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;


public class InfoApplication extends Application {
    private static boolean run = false;
    @Override
    public void start(Stage stage) throws Exception {
        if(!run) {
            run = true;
            FXMLLoader fxmlLoader = new FXMLLoader(InfoApplication.class.getResource("info.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 250, 600);
            scene.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());
            stage.setTitle("Tactic battle rush");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.setOnCloseRequest((event) -> {
                run = false;
            });
            stage.show();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
