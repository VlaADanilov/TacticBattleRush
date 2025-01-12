package org.example.client.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import org.example.client.HelloApplication;
import org.example.client.util.Images;
import org.example.client.util.WhoWinner;

import java.io.IOException;

public class RezultController {
    @FXML
    private ImageView rezultImage;
    @FXML
    private Button exitButton;

    public void initialize() {
        rezultImage.setImage(Images.getRezultImage(WhoWinner.getWinner()));
    }

    public void exit(MouseEvent actionEvent) {
        try {
            HelloApplication.changeScene("hello-view.fxml");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
