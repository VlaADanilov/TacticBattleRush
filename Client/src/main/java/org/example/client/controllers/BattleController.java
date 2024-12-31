package org.example.client.controllers;

import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import org.example.client.GameEntities.AbstractEntity;
import org.example.client.GameEntities.elements.AbstractElement;
import org.example.client.GameEntities.soldiers.AbstractSoldier;
import org.example.client.board.BoardSingleton;
import org.example.client.connectors.ClientImpl;
import org.example.client.util.Images;

public class BattleController {
    @FXML
    private GridPane gridPane;
    boolean hod;

    public void initialize(){
        BoardSingleton.getInstance().readCoordinateMessage(ClientImpl.getInstance().getMessage().getData());
        AbstractEntity[][] board = BoardSingleton.getInstance().getBoard();
        for(int y = 0; y < gridPane.getColumnCount(); y++){
            for(int x = 0; x < gridPane.getRowCount(); x++){
                AbstractEntity entity = board[x][y];
                if(entity == null){
                    Pane pane = new Pane();
                    pane.setStyle("-fx-border-color: black");
                    gridPane.add(pane, y, x);
                }else{
                    if(entity instanceof AbstractElement){
                        Pane pane = new Pane();
                        ImageView imageView = new ImageView(
                                Images.getElementImage(((AbstractElement) entity).getIndex())
                        );
                        imageView.setFitHeight(gridPane.getMaxHeight() / gridPane.getRowCount());
                        imageView.setFitWidth(gridPane.getMaxWidth() / gridPane.getColumnCount());
                        pane.getChildren().add(imageView);
                        gridPane.add(pane, y, x);
                    }else{
                        if(entity instanceof AbstractSoldier){
                            Pane pane = new Pane();
                            ImageView imageView = new ImageView(
                                    Images.getSoldierImage(((AbstractSoldier) entity).getINDEX())
                            );
                            imageView.setFitHeight(gridPane.getMaxHeight() / gridPane.getRowCount());
                            imageView.setFitWidth(gridPane.getMaxWidth() / gridPane.getColumnCount());
                            pane.getChildren().add(imageView);
                            gridPane.add(pane, y, x);
                        }
                    }
                }
            }
        }
    }
}
