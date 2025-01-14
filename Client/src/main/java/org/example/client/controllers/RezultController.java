package org.example.client.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import org.example.client.HelloApplication;
import org.example.client.util.Images;
import org.example.client.util.WhoWinner;

import java.io.IOException;

public class RezultController {
    @FXML
    private ImageView rezultImage;


    public void initialize() {

        rezultImage.setPreserveRatio(false); // Сохранять пропорции
        rezultImage.setSmooth(true);  // Сглаживание изображения
        rezultImage.setCache(true); // Кэширование изображения

        rezultImage.setImage(Images.getRezultImage(WhoWinner.getWinner()));
    }

    public void exit() {
        try {
            HelloApplication.changeScene("hello-view.fxml");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
