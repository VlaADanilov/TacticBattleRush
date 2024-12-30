package org.example.client.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import org.example.client.GameEntities.fabrica.ElementFabrica;
import org.example.client.board.BoardSingleton;
import org.example.client.connectors.ClientImpl;

public class setUnitController {
    @FXML
    private GridPane gridPane;
    @FXML
    private ImageView heavyKnightImage;
    @FXML
    private ImageView archerImage;
    @FXML
    private ImageView hillerImage;
    @FXML
    private ImageView horseKnightImage;
    @FXML
    private Button submitButton;

    private int cnt = 0;

    private int choice;

    public void initialize() {
        byte[] arr = ClientImpl.getInstance().getLastMessage().getData();
        for(int i = 0; i < arr.length; i += 3){
            BoardSingleton.getInstance().addElement(ElementFabrica.getElement(arr[i+2]), arr[i], arr[i+1]);
            Pane pane = new Pane();
            pane.setStyle("-fx-background-color: red;");
            gridPane.add(pane, arr[i],arr[i+1]);
        }




        heavyKnightImage.setImage(new Image(this.getClass().getResourceAsStream("/image/меч.jpg")));
        archerImage.setImage(new Image(this.getClass().getResourceAsStream("/image/лук.png")));
        hillerImage.setImage(new Image(this.getClass().getResourceAsStream("/image/лечение.png")));
        horseKnightImage.setImage(new Image(this.getClass().getResourceAsStream("/image/лошадь.png")));

        submitButton.setDisable(true);
    }
}
