package org.example.client.controllers;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.example.client.GameEntities.AbstractEntity;
import org.example.client.GameEntities.elements.AbstractElement;
import org.example.client.GameEntities.soldiers.*;
import org.example.client.HelloApplication;
import org.example.client.board.BoardSingleton;
import org.example.client.board.tools.SoldierWithIndexAndCoordinats;
import org.example.client.connectors.ClientImpl;
import org.example.client.protocol.Message;
import org.example.client.protocol.exception.ExceedingTheMaximumLengthException;
import org.example.client.protocol.exception.WrongMessageTypeException;
import org.example.client.util.Images;
import org.example.client.util.MyStyle;
import org.example.client.util.Player;
import org.example.client.util.SoldierSpeciality;

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

    public void initialize() {
        myService = getMyService();
        hod = BoardSingleton.getInstance().readCoordinateMessage(ClientImpl.getInstance().getLastMessage().getData()) == 1;
        AbstractEntity[][] board = BoardSingleton.getInstance().getBoard();
        for (int y = 0; y < gridPane.getColumnCount(); y++) {
            for (int x = 0; x < gridPane.getRowCount(); x++) {
                AbstractEntity entity = board[x][y];
                Pane pane = new Pane();
                pane.setStyle("-fx-background-color: %s;-fx-border-color: black;".formatted(BOARD_COLOR));
                if (entity == null) {
                    gridPane.add(pane, y, x);
                } else {
                    if (entity instanceof AbstractElement) {
                        ImageView imageView = new ImageView(
                                Images.getElementImage(((AbstractElement) entity).getIndex())
                        );


                        imageView.setFitHeight(gridPane.getMaxHeight() / gridPane.getRowCount());
                        imageView.setFitWidth(gridPane.getMaxWidth() / gridPane.getColumnCount());
                        pane.getChildren().add(imageView);
                        gridPane.add(pane, y, x);
                    } else {
                        if (entity instanceof AbstractSoldier) {
                            ImageView imageView = new ImageView(
                                    Images.getSoldierImage(((AbstractSoldier) entity).getINDEX())
                            );


                            AbstractSoldier soldier = (AbstractSoldier) entity;
                            if (BoardSingleton.getInstance().isMySoldier(soldier)) {
                                pane.setStyle("-fx-background-color: blue");
                            } else {
                                pane.setStyle("-fx-background-color: red");
                            }

                            imageView.setFitHeight(gridPane.getMaxHeight() / gridPane.getRowCount());
                            imageView.setFitWidth(gridPane.getMaxWidth() / gridPane.getColumnCount());
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
            label.setOnMouseClicked((event -> {
                if (hod) {
                    if (BoardSingleton.getInstance().isMySoldier(soldier.getIndex())) {
                        if (choice == 0) {
                            choice = soldier.getIndex();
                            editBoardByDoingMovementSet(soldier.getCol(), soldier.getRow(), soldier.getSoldier().getMovementradius(), "grey");
                            editBoardByDoingAttackSet(soldier.getCol(), soldier.getRow(), soldier.getSoldier().getDamageRadius(), true);
                        } else {
                            if (choice != soldier.getIndex()) {
                                SoldierWithIndexAndCoordinats tempSold = BoardSingleton.getInstance().getMySoldierByIndex(choice);
                                editBoardByDoingMovementSet(tempSold.getCol(), tempSold.getRow(), tempSold.getSoldier().getMovementradius(), BOARD_COLOR);
                                editBoardByDoingAttackSet(tempSold.getCol(), tempSold.getRow(), tempSold.getSoldier().getDamageRadius(), false);
                                style.setStyle("-fx-background-color: blue");
                                choice = soldier.getIndex();
                                editBoardByDoingMovementSet(soldier.getCol(), soldier.getRow(), soldier.getSoldier().getMovementradius(), "grey");
                                editBoardByDoingAttackSet(soldier.getCol(), soldier.getRow(), soldier.getSoldier().getDamageRadius(), true);
                                return;
                            }
                            choice = 0;
                            editBoardByDoingMovementSet(soldier.getCol(), soldier.getRow(), soldier.getSoldier().getMovementradius(), BOARD_COLOR);
                            editBoardByDoingAttackSet(soldier.getCol(), soldier.getRow(), soldier.getSoldier().getDamageRadius(), false);
                        }
                    }
                }
            }));
            label.setOnMouseEntered((event -> {
                if (BoardSingleton.getInstance().isOpponentSoldier(soldier.getIndex())) {
                    editBoardByChoicingOpponent(soldier.getCol(), soldier.getRow(), true, style);
                }
                if (BoardSingleton.getInstance().isMySoldier(soldier.getIndex())) {
                    editBoardByChoicingMy(soldier.getCol(), soldier.getRow(), true, style);
                }
            }));
            label.setOnMouseExited((event -> {
                if (BoardSingleton.getInstance().isOpponentSoldier(soldier.getIndex())) {
                    editBoardByChoicingOpponent(soldier.getCol(), soldier.getRow(), false, style);
                }
                if (BoardSingleton.getInstance().isMySoldier(soldier.getIndex())) {
                    editBoardByChoicingMy(soldier.getCol(), soldier.getRow(), false, style);
                }
            }));
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

                        editBoardByDoingMovementSet(entry.getKey(), entry.getValue(), soldier.getSoldier().getMovementradius(), "grey");
                        editBoardByDoingAttackSet(entry.getKey(), entry.getValue(), soldier.getSoldier().getDamageRadius(), true);
                    } else {
                        if (choice != soldier.getIndex()) {
                            return;
                        }
                        choice = 0;
                        editBoardByDoingMovementSet(entry.getKey(), entry.getValue(), soldier.getSoldier().getMovementradius(), BOARD_COLOR);
                        editBoardByDoingAttackSet(entry.getKey(), entry.getValue(), soldier.getSoldier().getDamageRadius(), false);
                    }
                }
            }));

        }
    }

    private int editBoardByDoingAttackSet(Integer column, Integer row, int damageradius, boolean flag) {
        int result = 0;
        AbstractEntity[][] board = BoardSingleton.getInstance().getBoard();
        for (int tempColumn = column - damageradius; tempColumn <= column + damageradius; tempColumn++) {
            if (tempColumn < 0 || tempColumn > 14) continue;
            for (int tempRow = row - damageradius; tempRow <= row + damageradius; tempRow++) {
                if (tempRow < 0 || tempRow > 14) continue;
                if (tempColumn == column && tempRow == row) continue;

                if (board[tempRow][tempColumn] != null && board[tempRow][tempColumn] instanceof AbstractSoldier) {
                    int finalTempColumn = tempColumn;
                    int finalTempRow = tempRow;
                    Pane pane = (Pane) gridPane.getChildren().stream()
                            .filter((ent) -> Objects.equals(GridPane.getColumnIndex(ent), finalTempColumn) && Objects.equals(GridPane.getRowIndex(ent), finalTempRow)).findAny().orElseThrow();
                    if (flag) {
                        int soldierIndex = BoardSingleton.getInstance().getSoldier(finalTempColumn, finalTempRow).getIndex();
                        if ((SoldierSpeciality.isActionForOpponent(BoardSingleton.getInstance().getSoldier(column, row).getSoldier())
                                && BoardSingleton.getInstance().isOpponentSoldier(soldierIndex))
                                || (!SoldierSpeciality.isActionForOpponent(BoardSingleton.getInstance().getSoldier(column, row).getSoldier())
                                && BoardSingleton.getInstance().isMySoldier(soldierIndex))) {
                            if (!hasObstacleInPathAttack(finalTempRow, finalTempColumn, row, column)) {
                                result++;
                                pane.setStyle("-fx-background-color: #CF4658FF");
                                pane.setOnMouseClicked((event) -> {
                                    if (hod && choice != 0) {
                                        SoldierWithIndexAndCoordinats mySoldierByCoordinates = BoardSingleton.getInstance().getMySoldierByCoordinates(column, row);
                                        editBoardByDoingMovementSet(column, row, mySoldierByCoordinates.getSoldier().getMovementradius(), BOARD_COLOR);
                                        editBoardByDoingAttackSet(column, row, damageradius, false);
                                        SoldierWithIndexAndCoordinats soldier = BoardSingleton.getInstance().getSoldier(finalTempColumn, finalTempRow);
                                        action(Player.You, choice, soldier.getIndex());
                                        if (array == null) {
                                            try {
                                                ClientImpl.getInstance().sendMessage(
                                                        Message.createMessage(4, new byte[]{(byte) choice, (byte) soldier.getIndex()})
                                                );
                                            } catch (ExceedingTheMaximumLengthException e) {
                                                throw new RuntimeException(e);
                                            } catch (WrongMessageTypeException e) {
                                                throw new RuntimeException(e);
                                            }
                                        } else {
                                            array[3] = (byte) choice;
                                            array[4] = (byte) soldier.getIndex();
                                            try {
                                                ClientImpl.getInstance().sendMessage(
                                                        Message.createMessage(4, array)
                                                );
                                            } catch (ExceedingTheMaximumLengthException e) {
                                                throw new RuntimeException(e);
                                            } catch (WrongMessageTypeException e) {
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
                        }
                    } else {
                        SoldierWithIndexAndCoordinats soldier = BoardSingleton.getInstance().getSoldier(finalTempColumn, finalTempRow);
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
        }
        return result;
    }


    private boolean hasObstacleInPathAttack(int targetRow, int targetColumn, int unitRow, int unitColumn) {
        // Если цель совпадает с персонажем, препятствий нет
        if (targetRow == unitRow && targetColumn == unitColumn) {
            return false;
        }

        // Определяем направление выстрела
        int dx = Integer.compare(targetRow, unitRow); // -1 (вверх), 0 (нет по строкам), 1 (вниз)
        int dy = Integer.compare(targetColumn, unitColumn); // -1 (влево), 0 (нет по столбцам), 1 (вправо)

        if (isObstacle(targetRow, targetColumn)) {
            return true;
        }

        if (dx == 0 || dy == 0) {
            if (dx == 0) {
                return hasObstacleInPathAttack(targetRow, targetColumn - dy, unitRow, unitColumn);
            } else {
                return hasObstacleInPathAttack(targetRow - dx, targetColumn, unitRow, unitColumn);
            }
        } else {
            if(Math.abs(targetRow - unitRow) == 1 && Math.abs(targetColumn - unitColumn) == 1) {
                return false;
            }
            if(isObstacle(targetRow - dx, targetColumn) || isObstacle(targetRow, targetColumn - dy)) {
                if(isObstacle(targetRow - dx, targetColumn)){
                    return Math.abs(targetColumn - unitColumn) < Math.abs(targetRow - unitRow);
                }else{
                    return Math.abs(targetColumn - unitColumn) > Math.abs(targetRow - unitRow);
                }
            }
            return hasObstacleInPathAttack(targetRow - dx, targetColumn - dy, unitRow, unitColumn);
        }
    }

    private boolean hasObstacleWalking(int targetRow, int targetColumn, int unitRow, int unitColumn){
        // Если цель совпадает с персонажем, препятствий нет
        if (targetRow == unitRow && targetColumn == unitColumn) {
            return false;
        }

        // Определяем направление выстрела
        int dx = Integer.compare(targetRow, unitRow); // -1 (вверх), 0 (нет по строкам), 1 (вниз)
        int dy = Integer.compare(targetColumn, unitColumn); // -1 (влево), 0 (нет по столбцам), 1 (вправо)

//         Если цель по прямой, то проверяем все на пути
        if (dx == 0 || dy == 0){
            int currentX = unitRow + dx;
            int currentY = unitColumn + dy;

            // Движемся от персонажа к цели и проверяем каждую клетку на наличие препятствий
            while (currentX != targetRow || currentY != targetColumn) {
                if (isObstacle(currentX, currentY)) {
                    return true; // Если найдено препятствие, то возвращаем true
                }

                currentX += dx;
                currentY += dy;
            }
            return false;
        }
        // Если цель по диагонали, то проверяем только на наличие препятствия непосредственно в клетке перед целью
        int nextX = targetRow - dx;
        int nextY = targetColumn - dy;

        return isObstacle(nextX, nextY);
    }

    // Метод для проверки, является ли клетка препятствием (камень или дерево)
    private boolean isObstacle(int row, int column) {
        if (row >= 0 && row < BoardSingleton.getInstance().getBoard().length && column >= 0 && column < BoardSingleton.getInstance().getBoard()[0].length) {
            if (BoardSingleton.getInstance().getBoard()[row][column] == null) {
                return false;
            } else {
                AbstractEntity ent = BoardSingleton.getInstance().getBoard()[row][column];
                return ent instanceof AbstractElement;
            }
        }
        return true; // Если координаты выходят за границы поля - считаем это препятствием
    }

    private void action(Player player, int attacker, int defender) {
        addHistory(player, attacker, defender);
        SoldierWithIndexAndCoordinats soldier = BoardSingleton.getInstance().action(attacker, defender);
        map.get(defender).setProgress(
                soldier.getSoldier().getHealth() / (soldier.getSoldier().getMAXHEALT() + 0d)
        );
        if (soldier.getSoldier().getHealth() == 0) {
            Pane pane = (Pane) gridPane.getChildren().stream()
                    .filter((entity) -> Objects.equals(GridPane.getColumnIndex(entity), soldier.getCol()) &&
                            Objects.equals(GridPane.getRowIndex(entity), soldier.getRow())).findAny().orElseThrow();
            gridPane.getChildren().remove(pane);
            Pane newPane = new Pane();
            newPane.setStyle("-fx-border-color: black; -fx-background-color: %s".formatted(BOARD_COLOR));
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

    private void editBoardByDoingMovementSet(Integer column, Integer row, int movementradius, String color) {
        AbstractEntity[][] board = BoardSingleton.getInstance().getBoard();
        for (int tempColumn = column - movementradius; tempColumn <= column + movementradius; tempColumn++) {
            if (tempColumn < 0 || tempColumn > 14) continue;
            for (int tempRow = row - movementradius; tempRow <= row + movementradius; tempRow++) {
                if (tempRow < 0 || tempRow > 14) continue;
                if (board[tempRow][tempColumn] == null) {
                    int finalTempColumn = tempColumn;
                    int finalTempRow = tempRow;
                    if (!hasObstacleWalking(finalTempRow, finalTempColumn, row, column)) {
                        Pane pane = (Pane) gridPane.getChildren().stream()
                                .filter((ent) -> Objects.equals(GridPane.getColumnIndex(ent), finalTempColumn) && Objects.equals(GridPane.getRowIndex(ent), finalTempRow)).findAny().orElseThrow();
                        pane.setStyle("-fx-background-color: %s; -fx-border-color: black".formatted(color));
                        if (color.equals(BOARD_COLOR)) {
                            pane.setOnMouseClicked((event -> {
                            }));
                        }
                        //TODO onCLick
                        if (color.equals("grey")) {
                            pane.setOnMouseClicked((event -> {
                                if (hod && choice != 0) {
                                    SoldierWithIndexAndCoordinats mySoldierByCoordinates = BoardSingleton.getInstance().getMySoldierByCoordinates(column, row);
                                    editBoardByDoingMovementSet(column, row, movementradius, BOARD_COLOR);
                                    editBoardByDoingAttackSet(column, row, mySoldierByCoordinates.getSoldier().getDamageRadius(), false);
                                    move(Player.You, choice, finalTempColumn, finalTempRow);
                                    int result = 0;
                                    if (!SoldierSpeciality.oneDoingByHod(mySoldierByCoordinates.getSoldier())) {
                                        result = editBoardByDoingAttackSet(mySoldierByCoordinates.getCol(), mySoldierByCoordinates.getRow(), mySoldierByCoordinates.getSoldier().getDamageRadius(), true);
                                    }
                                    if (result == 0) {

                                        try {
                                            ClientImpl.getInstance().sendMessage(
                                                    Message.createMessage(4, new byte[]{(byte) choice, (byte) finalTempColumn, (byte) finalTempRow})
                                            );
                                        } catch (ExceedingTheMaximumLengthException e) {
                                            throw new RuntimeException(e);
                                        } catch (WrongMessageTypeException e) {
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
                                        array = new byte[]{(byte) choice, (byte) finalTempColumn, (byte) finalTempRow, 0, 0};
                                    }
                                }
                            }));
                        }
                    }
                }
            }
        }
    }

    private MyService getMyService() {
        MyService myService = new MyService();
        myService.setOnSucceeded((event1 -> {
            Message message = ClientImpl.getInstance().getLastMessage();
            if (message.getType() == 4) {
                if (message.getData().length == 3) {
                    int index = message.getData()[0];
                    int column1 = message.getData()[1];
                    int row1 = message.getData()[2];
                    move(Player.Opponent, index, column1, row1);
                } else {
                    if (message.getData().length == 2) {
                        int attacker = message.getData()[0];
                        int defender = message.getData()[1];
                        action(Player.Opponent, attacker, defender);
                    } else {
                        int index = message.getData()[0];
                        int column1 = message.getData()[1];
                        int row1 = message.getData()[2];
                        move(Player.Opponent, index, column1, row1);
                        int attacker = message.getData()[3];
                        int defender = message.getData()[4];
                        action(Player.Opponent, attacker, defender);
                    }
                }

                boolean flag = false;
                for (int i = 1; i <= map.size(); i++) {
                    if (BoardSingleton.getInstance().isMySoldier(i)) {
                        flag = flag || map.get(i).getProgress() != 0;
                    }
                }
                if (!flag) {
                    System.out.println("Ты проиграл");
                    BoardSingleton.getInstance().clear();
                    ClientImpl.getInstance().disconnect();
                    try {
                        HelloApplication.changeScene("hello-view.fxml");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                hod = true;
                hodLabel.setText("You are going");
                editBoardByDoingMovement();
                array = null;
                myService.reset();
            } else {
                //TODO нужна нормальная логика
                if (message.getType() == 5) {
                    if (message.getData()[0] == 1) {
                        System.out.println("Ты выиграл");
                    } else {
                        System.out.println("Ты проиграл");
                    }
                    BoardSingleton.getInstance().clear();
                    ClientImpl.getInstance().disconnect();
                    try {
                        HelloApplication.changeScene("hello-view.fxml");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }));
        myService.setOnFailed((event2) -> myService.getException().printStackTrace());
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
        if (soldier instanceof HeavyKnight) {
            return "Heavy knight";
        }
        if (soldier instanceof Archer) {
            return "Archer";
        }
        if (soldier instanceof Hiller) {
            return "Hiller";
        }
        if (soldier instanceof HorseKnight) {
            return "Horse knight";
        }
        return "WTF";
    }

    private static class MyService extends Service<Boolean> {

        @Override
        protected Task<Boolean> createTask() {
            return new Task<>() {

                @Override
                protected Boolean call() {

                    try {
                        ClientImpl.getInstance().sendMessage(
                                Message.createMessage(4, new byte[0])
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
