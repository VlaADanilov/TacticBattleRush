package org.example.client.controllers;

import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.example.client.HelloApplication;
import org.example.client.InfoApplication;
import org.example.client.board.BoardSingleton;
import org.example.client.board.tools.SoldierWithIndexAndCoordinats;
import org.example.client.connectors.ClientImpl;
import org.example.client.util.Images;
import org.example.client.util.MyStyle;
import org.example.client.util.Player;
import org.example.client.util.WhoWinner;
import ru.itis.prot.gameEntities.SoldierSpeciality;
import ru.itis.prot.gameEntities.AbstractEntity;
import ru.itis.prot.gameEntities.soldiers.*;
import ru.itis.prot.gameEntities.elements.*;
import ru.itis.prot.protocol.Message;
import ru.itis.prot.protocol.exception.*;

import java.io.IOException;
import java.util.*;

public class BattleController {
    @FXML
    private TextArea historyTextArea;
    @FXML
    private Label hodLabel;
    @FXML
    private VBox healthBox;
    @FXML
    private GridPane gridPane;
    boolean hod;
    private Map<Integer, ProgressBar> map;
    private int choice = 0;
    private MyService myService;
    private byte[] array;
    private static final String BOARD_COLOR = "#38FF25FF";

    private double imagesWidth;
    private double imagesHeight;

    public void initialize() {
        imagesWidth = gridPane.getMaxWidth() / gridPane.getColumnCount();
        imagesHeight = gridPane.getMaxHeight() / gridPane.getColumnCount();
        myService = getMyService();
        hod = BoardSingleton.getInstance().readCoordinateMessage(ClientImpl.getInstance().getLastMessage().getData()) == 1;
        AbstractEntity[][] board = BoardSingleton.getInstance().getBoard();
        for (int y = 0; y < gridPane.getColumnCount(); y++) {
            for (int x = 0; x < gridPane.getRowCount(); x++) {
                AbstractEntity entity = board[x][y];
                Pane pane = new Pane();
                pane.setBackground(new Background(new BackgroundImage(
                        Images.getGrassImage(),
                        BackgroundRepeat.NO_REPEAT,
                        BackgroundRepeat.NO_REPEAT,
                        BackgroundPosition.CENTER,
                        new BackgroundSize(
                                imagesWidth,
                                imagesHeight,
                                false,
                                false,
                                true,
                                true
                        )
                        )));
                pane.setStyle("-fx-border-color: black");
                //pane.setStyle("-fx-background-color: %s;-fx-border-color: black;".formatted(BOARD_COLOR));
                if (entity == null) {
                    gridPane.add(pane, y, x);
                } else {
                    if (entity instanceof AbstractElement) {
                        ImageView imageView = new ImageView(
                                Images.getElementImage(((AbstractElement) entity).getIndex())
                        );


                        imageView.setFitHeight(imagesHeight);
                        imageView.setFitWidth(imagesWidth);
                        pane.getChildren().add(imageView);
                        gridPane.add(pane, y, x);
                    } else {
                        if (entity instanceof AbstractSoldier soldier) {
                            ImageView imageView = new ImageView(
                                    Images.getSoldierImage(((AbstractSoldier) entity).getINDEX())
                            );


                            if (BoardSingleton.getInstance().isMySoldier(soldier)) {
                                pane.setStyle("-fx-background-color: blue");
                            } else {
                                pane.setStyle("-fx-background-color: red");
                            }

                            imageView.setFitHeight(imagesHeight);
                            imageView.setFitWidth(imagesWidth);
                            pane.getChildren().add(imageView);
                            gridPane.add(pane, y, x);
                        }
                    }
                }
            }
        }

        map = new HashMap<>();

        addHealthBars(BoardSingleton.getInstance().getMySoldiers());
        addHealthBars(BoardSingleton.getInstance().getOpponentSoldiers());

        editBoardByDoingMovement();

        if (hod) {
            hodLabel.setText("You are going");
        } else {
            hodLabel.setText("Opponent is going");
            if (myService.getState() == Worker.State.READY
                    || myService.getState() == Worker.State.SCHEDULED) {
                myService.start();
            }
        }
    }

    private void addHealthBars(List<SoldierWithIndexAndCoordinats> soldiers) {
        for (SoldierWithIndexAndCoordinats soldier : soldiers) {
            ProgressBar progressBar = new ProgressBar(1);
            progressBar.setStyle(
                    "-fx-accent: red; " +  // Зеленый цвет прогресса
                            "-fx-background-color: grey; " + // Светло-серый фон
                            "-fx-border-color: #ccc; " + // Серая граница
                            "-fx-border-width: 1px; " +
                            "-fx-border-radius: 5px; "
            );
            progressBar.setMaxWidth(200);
            progressBar.setPrefWidth(200);
            map.put(soldier.getIndex(), progressBar);
            Label label = new Label(soldier.getIndex() + " " + nameSoldier(soldier.getSoldier()));
            MyStyle style = new MyStyle();
            EventHandler<MouseEvent> click = event -> {
                if (hod) {
                    if (BoardSingleton.getInstance().isMySoldier(soldier.getIndex())) {
                        if (choice == 0) {
                            choice = soldier.getIndex();
                            editBoardByDoingMovementSet(soldier.getCol(), soldier.getRow(), "grey");
                            editBoardByDoingAttackSet(soldier.getCol(), soldier.getRow(), true);
                        } else {
                            if (choice != soldier.getIndex()) {
                                SoldierWithIndexAndCoordinats tempSold = BoardSingleton.getInstance().getMySoldierByIndex(choice);
                                editBoardByDoingMovementSet(tempSold.getCol(), tempSold.getRow(), BOARD_COLOR);
                                editBoardByDoingAttackSet(tempSold.getCol(), tempSold.getRow(), false);
                                style.setStyle("-fx-background-color: blue");
                                choice = soldier.getIndex();
                                editBoardByDoingMovementSet(soldier.getCol(), soldier.getRow(), "grey");
                                editBoardByDoingAttackSet(soldier.getCol(), soldier.getRow(), true);
                                return;
                            }
                            choice = 0;
                            editBoardByDoingMovementSet(soldier.getCol(), soldier.getRow(), BOARD_COLOR);
                            editBoardByDoingAttackSet(soldier.getCol(), soldier.getRow(), false);
                        }
                    }
                }
            };
            label.setOnMouseClicked(click);
            progressBar.setOnMouseClicked(click);
            EventHandler<MouseEvent> mouseEntered = event -> {
                if (BoardSingleton.getInstance().isOpponentSoldier(soldier.getIndex())) {
                    editBoardByChoicingOpponent(soldier.getCol(), soldier.getRow(), true, style);
                }
                if (BoardSingleton.getInstance().isMySoldier(soldier.getIndex())) {
                    editBoardByChoicingMy(soldier.getCol(), soldier.getRow(), true, style);
                }
            };

            label.setOnMouseEntered(mouseEntered);
            progressBar.setOnMouseEntered(mouseEntered);
            EventHandler<MouseEvent> mouseExited = event -> {
                if (BoardSingleton.getInstance().isOpponentSoldier(soldier.getIndex())) {
                    editBoardByChoicingOpponent(soldier.getCol(), soldier.getRow(), false, style);
                }
                if (BoardSingleton.getInstance().isMySoldier(soldier.getIndex())) {
                    editBoardByChoicingMy(soldier.getCol(), soldier.getRow(), false, style);
                }
            };

            label.setOnMouseExited(mouseExited);
            progressBar.setOnMouseExited(mouseExited);
            healthBox.getChildren().add(label);
            healthBox.getChildren().add(progressBar);
        }
    }

    private void editBoardByChoicingMy(int column, int row, boolean flag, MyStyle style) {
        for (Node node : gridPane.getChildren()) {
            if (Objects.equals(GridPane.getColumnIndex(node), column) && Objects.equals(GridPane.getRowIndex(node), row)) {
                Pane pane = (Pane) node;
                if (flag) {
                    style.setStyle(pane.getStyle());
                    pane.setStyle("-fx-background-color: #081A8EFF");
                } else {
                    pane.setStyle(style.getStyle());
                }
                break;
            }
        }
    }

    private void editBoardByChoicingOpponent(int column, int row, boolean flag, MyStyle style) {
        for (Node node : gridPane.getChildren()) {
            if (Objects.equals(GridPane.getColumnIndex(node), column) && Objects.equals(GridPane.getRowIndex(node), row)) {
                Pane pane = (Pane) node;
                if (flag) {
                    style.setStyle(pane.getStyle());
                    pane.setStyle("-fx-background-color: #5E0505FF");
                } else {
                    pane.setStyle(style.getStyle());
                }
                break;
            }
        }
    }

    private void editBoardByDoingMovement() {
        List<Map.Entry<Integer, Integer>> mySoldiersCoordinates = BoardSingleton.getInstance().getMySoldiersCoordinates();
        for (Map.Entry<Integer, Integer> entry : mySoldiersCoordinates) {
            Pane pane = (Pane) gridPane.getChildren().stream()
                    .filter((ent) -> Objects.equals(GridPane.getColumnIndex(ent), entry.getKey()) && Objects.equals(GridPane.getRowIndex(ent), entry.getValue())).findAny().orElseThrow();
            SoldierWithIndexAndCoordinats soldier = BoardSingleton.getInstance().getMySoldierByCoordinates(entry.getKey(), entry.getValue());
            pane.setOnMouseClicked((event ->
            {
                if (hod && array == null) {
                    if (choice == 0) {
                        choice = soldier.getIndex();

                        editBoardByDoingMovementSet(entry.getKey(), entry.getValue(), "grey");
                        editBoardByDoingAttackSet(entry.getKey(), entry.getValue(), true);
                    } else {
                        if (choice != soldier.getIndex()) {
                            return;
                        }
                        choice = 0;
                        editBoardByDoingMovementSet(entry.getKey(), entry.getValue(), BOARD_COLOR);
                        editBoardByDoingAttackSet(entry.getKey(), entry.getValue(), false);
                    }
                }
            }));

        }
    }

    private int editBoardByDoingAttackSet(Integer column, Integer row, boolean flag) {
        SoldierWithIndexAndCoordinats soldier1 = BoardSingleton.getInstance().getSoldier(column, row);

        Set<Pair<Integer, Integer>> shootablePositions = BoardSingleton.getInstance().getShootablePositions(soldier1.getIndex());
        int result = 0;
        AbstractEntity[][] board = BoardSingleton.getInstance().getBoard();
        for(Pair<Integer, Integer> pair : shootablePositions) {
            if(board[pair.getKey()][pair.getValue()] != null && board[pair.getKey()][pair.getValue()] instanceof AbstractSoldier) {
                Pane pane = (Pane) gridPane.getChildren().stream()
                        .filter((ent) -> Objects.equals(GridPane.getColumnIndex(ent), pair.getValue()) && Objects.equals(GridPane.getRowIndex(ent), pair.getKey())).findAny().orElseThrow();
                if(flag){
                    int soldierIndex = BoardSingleton.getInstance().getSoldier(pair.getValue(), pair.getKey()).getIndex();
                    if ((SoldierSpeciality.isActionForOpponent(BoardSingleton.getInstance().getSoldier(column, row).getSoldier())
                            && BoardSingleton.getInstance().isOpponentSoldier(soldierIndex))
                            || (!SoldierSpeciality.isActionForOpponent(BoardSingleton.getInstance().getSoldier(column, row).getSoldier())
                            && BoardSingleton.getInstance().isMySoldier(soldierIndex))) {
                        result++;
                        pane.setStyle("-fx-background-color: #CF4658FF");
                        pane.setOnMouseClicked((event) -> {
                            if (hod && choice != 0) {
                                editBoardByDoingMovementSet(column, row, BOARD_COLOR);
                                editBoardByDoingAttackSet(column, row, false);
                                SoldierWithIndexAndCoordinats soldier = BoardSingleton.getInstance().getSoldier(pair.getValue(), pair.getKey());
                                action(Player.You, choice, soldier.getIndex());
                                if (array == null) {
                                    try {
                                        ClientImpl.getInstance().sendMessage(
                                                Message.createMessage(Message.TYPE_ATTACK, new byte[]{(byte) choice, (byte) soldier.getIndex()})
                                        );
                                    } catch (ExceedingTheMaximumLengthException | WrongMessageTypeException e) {
                                        throw new RuntimeException(e);
                                    }
                                } else {
                                    array[3] = (byte) choice;
                                    array[4] = (byte) soldier.getIndex();
                                    try {
                                        ClientImpl.getInstance().sendMessage(
                                                Message.createMessage(Message.TYPE_ATTACK_AND_MOVE, array)
                                        );
                                    } catch (ExceedingTheMaximumLengthException | WrongMessageTypeException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                                choice = 0;
                                hod = false;
                                hodLabel.setText("Opponent is going");
                                if (myService.getState() == Worker.State.READY
                                        || myService.getState() == Worker.State.SCHEDULED) {
                                    myService.start();
                                }
                            }
                        });
                    }
                }else{
                    SoldierWithIndexAndCoordinats soldier = BoardSingleton.getInstance().getSoldier(pair.getValue(), pair.getKey());
                    if (BoardSingleton.getInstance().isMySoldier(soldier.getIndex())) {
                        pane.setStyle("-fx-background-color: blue");
                    } else {
                        pane.setStyle("-fx-background-color: red");
                    }
                    pane.setOnMouseClicked((event) -> {
                    });
                    editBoardByDoingMovement();
                }
            }
        }
        return result;
    }


    private void action(Player player, int attacker, int defender) {
        addHistory(player, attacker, defender);
        SoldierWithIndexAndCoordinats soldier = BoardSingleton.getInstance().action(attacker, defender);
        map.get(defender).setProgress(
                soldier.getSoldier().getHealth() / (soldier.getSoldier().getMAXHEALTH() + 0d)
        );
        if (soldier.getSoldier().getHealth() == 0) {
            Pane pane = (Pane) gridPane.getChildren().stream()
                    .filter((entity) -> Objects.equals(GridPane.getColumnIndex(entity), soldier.getCol()) &&
                            Objects.equals(GridPane.getRowIndex(entity), soldier.getRow())).findAny().orElseThrow();
            gridPane.getChildren().remove(pane);
            Pane newPane = new Pane();
            newPane.setBackground(new Background(new BackgroundImage(
                    Images.getGrassImage(),
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.CENTER,
                    new BackgroundSize(
                            imagesWidth,
                            imagesHeight,
                            false,
                            false,
                            true,
                            true
                    )
            )));
            newPane.setStyle("-fx-border-color: black");
            gridPane.add(newPane, soldier.getCol(), soldier.getRow());
        }
    }

    private void addHistory(Player player, int indexOne, int indexTwo) {
        String string = "%s: %s doing action to %s".formatted(
                player.getString(),
                "%s %s".formatted(indexOne, nameSoldier(BoardSingleton.getInstance().getSoldierByIndex(indexOne).getSoldier())),
                "%s %s".formatted(indexTwo, nameSoldier(BoardSingleton.getInstance().getSoldierByIndex(indexTwo).getSoldier()))
        );
        if (!historyTextArea.getText().isEmpty()) {
            historyTextArea.appendText("\n");
        }
        historyTextArea.appendText(string);
    }

    private void addHistory(Player player, int index, int row, int column) {
        String string = "%s: %s move to (%s,%s)".formatted(
                player.getString(),
                "%s %s".formatted(index, nameSoldier(BoardSingleton.getInstance().getSoldierByIndex(index).getSoldier())),
                row,
                column
        );
        if (!historyTextArea.getText().isEmpty()) {
            historyTextArea.appendText("\n");
        }
        historyTextArea.appendText(string);
    }

    private void editBoardByDoingMovementSet(Integer column, Integer row, String color) {
        SoldierWithIndexAndCoordinats soldier = BoardSingleton.getInstance().getSoldier(column, row);
        Set<Pair<Integer, Integer>> movementPositions = BoardSingleton.getInstance().getMovementPositions(soldier.getIndex());
        for (Pair<Integer, Integer> pair : movementPositions) {
            Pane pane = (Pane) gridPane.getChildren().stream()
                    .filter((ent) -> Objects.equals(GridPane.getColumnIndex(ent), pair.getValue()) && Objects.equals(GridPane.getRowIndex(ent), pair.getKey())).findAny().orElseThrow();
            if(color.equals(BOARD_COLOR)){
                pane.setBackground(new Background(new BackgroundImage(
                        Images.getGrassImage(),
                        BackgroundRepeat.NO_REPEAT,
                        BackgroundRepeat.NO_REPEAT,
                        BackgroundPosition.CENTER,
                        new BackgroundSize(
                                imagesWidth,
                                imagesHeight,
                                false,
                                false,
                                true,
                                true
                        )
                )));
                pane.setStyle("-fx-border-color: black");
            }else {
                pane.setStyle("-fx-background-color: %s; -fx-border-color: black".formatted(color));
            }
            if (color.equals(BOARD_COLOR)) {
                pane.setOnMouseClicked((event -> {
                }));
            }
            if (color.equals("grey")) {
                int retCol = pair.getValue();
                int retRow = pair.getKey();
                pane.setOnMouseClicked((event -> {
                    if (hod && choice != 0) {
                        SoldierWithIndexAndCoordinats mySoldierByCoordinates = BoardSingleton.getInstance().getMySoldierByCoordinates(column, row);
                        editBoardByDoingMovementSet(column, row, BOARD_COLOR);
                        editBoardByDoingAttackSet(column, row, false);
                        move(Player.You, choice, pair.getValue(), pair.getKey());
                        int result = 0;
                        if (!SoldierSpeciality.oneDoingByHod(mySoldierByCoordinates.getSoldier())) {
                            result = editBoardByDoingAttackSet(mySoldierByCoordinates.getCol(), mySoldierByCoordinates.getRow(), true);
                        }
                        if (result == 0) {

                            try {
                                ClientImpl.getInstance().sendMessage(
                                        Message.createMessage(Message.TYPE_MOVE, new byte[]{(byte) choice, (byte) retCol, (byte) retRow})
                                );
                            } catch (ExceedingTheMaximumLengthException | WrongMessageTypeException e) {
                                throw new RuntimeException(e);
                            }
                            choice = 0;
                            hod = false;
                            hodLabel.setText("Opponent is going");
                            if (myService.getState() == Worker.State.READY
                                    || myService.getState() == Worker.State.SCHEDULED) {
                                myService.start();
                            }
                        } else {
                            array = new byte[]{(byte) choice, (byte) retCol, (byte) retRow, 0, 0};
                        }
                    }
                }));
            }
        }
    }

    private MyService getMyService() {
        MyService myService = new MyService();
        myService.setOnSucceeded((event1 -> {
            Message message = ClientImpl.getInstance().getLastMessage();
            if(message.getType() != Message.TYPE5) {
                if (message.getType() == Message.TYPE_ATTACK){
                    int attacker = message.getData()[0];
                    int defender = message.getData()[1];
                    action(Player.Opponent, attacker, defender);
                }else{
                    if(message.getType() == Message.TYPE_MOVE){
                        int index = message.getData()[0];
                        int column1 = message.getData()[1];
                        int row1 = message.getData()[2];
                        move(Player.Opponent, index, column1, row1);
                    }else{
                        if(message.getType() == Message.TYPE_ATTACK_AND_MOVE){
                            int index = message.getData()[0];
                            int column1 = message.getData()[1];
                            int row1 = message.getData()[2];
                            move(Player.Opponent, index, column1, row1);
                            int attacker = message.getData()[3];
                            int defender = message.getData()[4];
                            action(Player.Opponent, attacker, defender);
                        }
                    }
                }
                hod = true;
                hodLabel.setText("You are going");
                editBoardByDoingMovement();
                array = null;
                myService.reset();
            } else {
                //TODO нужна нормальная логика
                if (message.getData()[0] == 1) {
                    WhoWinner.setWinner(1);
                } else {
                    WhoWinner.setWinner(0);
                }
                BoardSingleton.getInstance().clear();
                ClientImpl.getInstance().disconnect();
                try {
                    HelloApplication.changeScene("rezult.fxml");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        }));
        return myService;
    }


    private void move(Player player, int index, int column1, int row1) {
        addHistory(player, index, column1, row1);
        SoldierWithIndexAndCoordinats sold = BoardSingleton.getInstance().getSoldierByIndex(index);
        int soldRow = sold.getRow();
        int soldCol = sold.getCol();
        BoardSingleton.getInstance().move(index, column1, row1);

        Pane soldier1 = (Pane) gridPane.getChildren().stream()
                .filter((ent) -> Objects.equals(GridPane.getColumnIndex(ent), soldCol)
                        && Objects.equals(GridPane.getRowIndex(ent), soldRow)).findAny().orElseThrow();

        Pane temp1 = (Pane) gridPane.getChildren().stream()
                .filter((ent) -> Objects.equals(GridPane.getColumnIndex(ent), column1)
                        && Objects.equals(GridPane.getRowIndex(ent), row1)).findAny().orElseThrow();

        gridPane.getChildren().remove(soldier1);
        gridPane.getChildren().remove(temp1);

        gridPane.add(temp1, soldCol, soldRow);

        gridPane.add(soldier1, column1, row1);
    }

    private String nameSoldier(AbstractSoldier soldier) {
        return SoldierSpeciality.nameSoldier(soldier);
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
                protected Boolean call() {

                    try {
                        ClientImpl.getInstance().sendMessage(
                                Message.createMessage(Message.TYPE0, new byte[0])
                        );
                        ClientImpl.getInstance().getMessage();
                        updateValue(true);
                        return true;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                }
            };
        }
    }
}
