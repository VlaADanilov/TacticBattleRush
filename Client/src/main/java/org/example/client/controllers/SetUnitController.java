package org.example.client.controllers;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import org.example.client.GameEntities.AbstractEntity;
import org.example.client.GameEntities.elements.AbstractElement;
import org.example.client.GameEntities.fabrica.ElementFabrica;
import org.example.client.GameEntities.fabrica.SoldierFabrica;
import org.example.client.HelloApplication;
import org.example.client.board.BoardSingleton;
import org.example.client.connectors.ClientImpl;
import org.example.client.protocol.Message;
import org.example.client.protocol.exception.ExceedingTheMaximumLengthException;
import org.example.client.protocol.exception.WrongMessageTypeException;
import org.example.client.util.Images;


import java.io.IOException;
import java.util.Objects;

public class SetUnitController {
    private MyService service;
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
        heavyKnightImage.setImage(Images.getSoldierImage(1));
        archerImage.setImage(Images.getSoldierImage(2));
        hillerImage.setImage(Images.getSoldierImage(3));
        horseKnightImage.setImage(Images.getSoldierImage(4));

        submitButton.setDisable(true);

        byte[] arr = ClientImpl.getInstance().getLastMessage().getData();
        boolean hod = arr[0] == 1;
        for(int i = 1; i < arr.length; i += 3){
            AbstractElement element = ElementFabrica.getElement(arr[i+2]);
            BoardSingleton.getInstance().addElement(element, arr[i], arr[i+1]);
            int finalI = i;
            gridPane.getChildren().stream()
                    .filter((entity) -> Objects.equals(GridPane.getRowIndex(entity), (int) arr[finalI + 1])
                            && Objects.equals(GridPane.getColumnIndex(entity), (int) arr[finalI]))
                    .findAny().ifPresent(check -> gridPane.getChildren().remove(check));
            Pane pane = new Pane();
            ImageView imageView = new ImageView();
            imageView.setImage(Images.getElementImage(element.getIndex()));
            gridPane.add(pane, arr[i],arr[i+1]);

            imageView.setFitHeight(gridPane.getMaxHeight() / gridPane.getRowCount());
            imageView.setFitWidth(gridPane.getMaxWidth() / gridPane.getColumnCount());
            pane.getChildren().add(imageView);
        }

        for(int i = 0; i < gridPane.getRowCount(); i++){
            for(int j = 0; j < gridPane.getColumnCount(); j++){
                Pane pane = new Pane();
                if((j > 2 && hod) || (j < 12 && !hod)){
                    pane.setStyle("-fx-background-color: grey; -fx-border-color: black");
                }else{
                    pane.setStyle("-fx-border-color: black; -fx-border-width: 2;");
                    int finalJ = j;
                    int finalI = i;
                    pane.setOnMouseClicked((event -> {
                        if(choice == -1){
                            return;
                        }
                        if(choice != 0 && cnt <= 3 && BoardSingleton.getInstance().checkForSoldier(finalJ,finalI)){
                            BoardSingleton.getInstance().removeMySoldier(finalJ, finalI);
                            cnt--;
                            pane.getChildren().clear();
                            submitButton.setDisable(true);
                        }
                        else {
                            if (choice != 0 && cnt < 3 && BoardSingleton.getInstance().checkForNull(finalJ, finalI)) {
                                BoardSingleton.getInstance().addMySoldier(SoldierFabrica.getSoldier(choice), finalJ, finalI);
                                ImageView imageView = new ImageView(Images.getSoldierImage(choice));
                                imageView.setFitHeight(gridPane.getMaxHeight() / gridPane.getRowCount());
                                imageView.setFitWidth(gridPane.getMaxWidth() / gridPane.getColumnCount());
                                pane.getChildren().add(imageView);
                                cnt++;
                                if (cnt == 3) {
                                    submitButton.setDisable(false);
                                }
                            }
                        }
                    }));
                }
                gridPane.add(pane, j, i);
            }
        }
    }

    public void choiseHeavyKinght(MouseEvent mouseEvent) {
        archerImage.setOpacity(0.5d);
        hillerImage.setOpacity(0.5d);
        horseKnightImage.setOpacity(0.5d);
        choice = 1;
        heavyKnightImage.setOpacity(1);
    }

    public void choiceArcher(MouseEvent mouseEvent) {
        heavyKnightImage.setOpacity(0.5d);
        hillerImage.setOpacity(0.5d);
        horseKnightImage.setOpacity(0.5d);
        choice = 2;
        archerImage.setOpacity(1);
    }

    public void choiseHiller(MouseEvent mouseEvent) {
        archerImage.setOpacity(0.5d);
        heavyKnightImage.setOpacity(0.5d);
        horseKnightImage.setOpacity(0.5d);
        choice = 3;
        hillerImage.setOpacity(1);
    }

    public void choiseHorse(MouseEvent mouseEvent) {
        archerImage.setOpacity(0.5d);
        hillerImage.setOpacity(0.5d);
        heavyKnightImage.setOpacity(0.5d);
        choice = 4;
        horseKnightImage.setOpacity(1);
    }

    public void next(ActionEvent actionEvent) throws ExceedingTheMaximumLengthException, WrongMessageTypeException, IOException {
        ClientImpl.getInstance().sendMessage(
                Message.createMessage(3, BoardSingleton.getInstance().getMySoldiersMessage())
        );
        choice = -1;
        heavyKnightImage.setDisable(true);
        heavyKnightImage.setOpacity(0);
        archerImage.setDisable(true);
        archerImage.setOpacity(0);
        hillerImage.setDisable(true);
        hillerImage.setOpacity(0);
        horseKnightImage.setDisable(true);
        horseKnightImage.setOpacity(0);

        service = new MyService();

        service.setOnSucceeded((event) -> {
            try {
                HelloApplication.changeScene("battle.fxml");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        service.start();
    }

    private static class MyService extends Service<Boolean> {

        @Override
        protected Task<Boolean> createTask() {
            return new Task<Boolean>() {

                @Override
                protected Boolean call() throws Exception {
                    Message message = ClientImpl.getInstance().getMessage();
                    if(message.getType() == 3){
                        return true;
                    }else{
                        throw new RuntimeException();
                    }
                }
            };
        }
    }
}
