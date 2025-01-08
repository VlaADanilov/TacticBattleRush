package org.example.client.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.example.client.HelloApplication;

import java.io.IOException;

public class MainMenuController {
    @FXML
    private Button startButton;

    public void initialize() {
        startButton.setOnAction(event -> {
            try {
                HelloApplication.changeScene("hello-view.fxml");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
