package org.example.client;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

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
