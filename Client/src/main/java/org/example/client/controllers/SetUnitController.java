package org.example.client.controllers;

import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.example.client.InfoApplication;
import ru.itis.prot.gameEntities.fabrica.*;
import ru.itis.prot.gameEntities.elements.*;
import ru.itis.prot.protocol.Message;
import ru.itis.prot.protocol.exception.*;
import org.example.client.HelloApplication;
import org.example.client.board.BoardSingleton;
import org.example.client.connectors.ClientImpl;

import org.example.client.util.Images;


import java.io.IOException;
import java.util.Objects;

public class SetUnitController {
    @FXML
    private ImageView mortarImage;
    @FXML
    private Label cntOfUnits;
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


    private int needCount = 0;

    private int cnt = 0;

    private int choice;

    public void initialize() {



        GridPane.setHgrow(gridPane, Priority.ALWAYS);
        GridPane.setVgrow(gridPane, Priority.ALWAYS);

        heavyKnightImage.setImage(Images.getSoldierImage(1));
        archerImage.setImage(Images.getSoldierImage(2));
        hillerImage.setImage(Images.getSoldierImage(3));
        horseKnightImage.setImage(Images.getSoldierImage(4));
        mortarImage.setImage(Images.getSoldierImage(5));

        submitButton.setDisable(true);

        byte[] arr = ClientImpl.getInstance().getLastMessage().getData();
        boolean hod = arr[0] == 1;
        needCount = arr[1];
        cntOfUnits.setText("Need " + needCount + " units");
        for(int i = 2; i < arr.length; i += 3){
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

            imageView.setPreserveRatio(false); // Сохранять пропорции
            imageView.setSmooth(true);  // Сглаживание изображения
            imageView.setCache(true); // Кэширование изображения

            imageView.fitWidthProperty().bind(pane.widthProperty());
            imageView.fitHeightProperty().bind(pane.heightProperty());
//            imageView.setFitHeight(gridPane.getMinHeight() / gridPane.getRowCount());
//            imageView.setFitWidth(gridPane.getMinWidth() / gridPane.getColumnCount());

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
                        if(choice != 0 && cnt <= needCount && BoardSingleton.getInstance().checkForSoldier(finalJ,finalI)){
                            BoardSingleton.getInstance().removeMySoldier(finalJ, finalI);
                            cnt--;
                            pane.getChildren().clear();
                            submitButton.setDisable(true);
                        }
                        else {
                            if (choice != 0 && cnt < needCount && BoardSingleton.getInstance().checkForNull(finalJ, finalI)) {
                                BoardSingleton.getInstance().addMySoldier(SoldierFabrica.getSoldier(choice), finalJ, finalI);
                                ImageView imageView = new ImageView(Images.getSoldierImage(choice));
                                imageView.setPreserveRatio(false); // Сохранять пропорции
                                imageView.setSmooth(true);  // Сглаживание изображения
                                imageView.setCache(true); // Кэширование изображения

                                imageView.fitWidthProperty().bind(pane.widthProperty());
                                imageView.fitHeightProperty().bind(pane.heightProperty());
                                pane.getChildren().add(imageView);
                                cnt++;
                                if (cnt == needCount) {
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

    private void allOpacity(double value){
        mortarImage.setOpacity(value);
        heavyKnightImage.setOpacity(value);
        archerImage.setOpacity(value);
        hillerImage.setOpacity(value);
        horseKnightImage.setOpacity(value);
    }

    public void choiseHeavyKinght(MouseEvent mouseEvent) {
        allOpacity(0.5d);
        choice = 1;
        heavyKnightImage.setOpacity(1);
    }

    public void choiceArcher(MouseEvent mouseEvent) {
        allOpacity(0.5d);
        choice = 2;
        archerImage.setOpacity(1);
    }

    public void choiseHiller(MouseEvent mouseEvent) {
        allOpacity(0.5d);
        choice = 3;
        hillerImage.setOpacity(1);
    }

    public void choiseHorse(MouseEvent mouseEvent) {
        allOpacity(0.5d);
        choice = 4;
        horseKnightImage.setOpacity(1);
    }

    public void next(ActionEvent actionEvent) throws ExceedingTheMaximumLengthException, WrongMessageTypeException, IOException {
        ClientImpl.getInstance().sendMessage(
                Message.createMessage(Message.TYPE3, BoardSingleton.getInstance().getMySoldiersMessage())
        );
        choice = -1;
        mortarImage.setDisable(true);
        mortarImage.setOpacity(0);
        heavyKnightImage.setDisable(true);
        heavyKnightImage.setOpacity(0);
        archerImage.setDisable(true);
        archerImage.setOpacity(0);
        hillerImage.setDisable(true);
        hillerImage.setOpacity(0);
        horseKnightImage.setDisable(true);
        horseKnightImage.setOpacity(0);
        cntOfUnits.setOpacity(0);
        submitButton.setDisable(true);

        service = new MyService();

        service.setOnSucceeded((event) -> {
            if(ClientImpl.getInstance().getLastMessage().getType() == Message.TYPE5){
                if(ClientImpl.getInstance().getLastMessage().getData()[0] == 1){
                    System.out.println("Ты победил, у соперника проблемы...");
                    try {
                        HelloApplication.changeScene("main_menu.fxml");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                else{
                    System.out.println("WTF?");
                    throw new RuntimeException();
                }
            }
            else {
                try {
                    HelloApplication.changeScene("battle.fxml");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        service.start();
    }

    public void choiceMortar(MouseEvent mouseEvent) {
        allOpacity(0.5d);
        choice = 5;
        mortarImage.setOpacity(1);
    }

    public void aboutUnits(ActionEvent actionEvent) {
        Platform.runLater(() -> {
            try {
                new InfoApplication().start(new Stage());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    private static class MyService extends Service<Boolean> {

        @Override
        protected Task<Boolean> createTask() {
            return new Task<>() {

                @Override
                protected Boolean call() throws Exception {
                    Message message = ClientImpl.getInstance().getMessage();
                    if(message.getType() == Message.TYPE3 || message.getType() == Message.TYPE5){
                        return true;
                    }else{
                        throw new RuntimeException();
                    }
                }
            };
        }
    }
}
