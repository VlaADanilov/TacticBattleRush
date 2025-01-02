package org.example.client.controllers;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.example.client.GameEntities.AbstractEntity;
import org.example.client.GameEntities.elements.AbstractElement;
import org.example.client.GameEntities.soldiers.*;
import org.example.client.board.BoardSingleton;
import org.example.client.board.tools.SoldierWithIndexAndCoordinats;
import org.example.client.connectors.ClientImpl;
import org.example.client.protocol.Message;
import org.example.client.protocol.exception.ExceedingTheMaximumLengthException;
import org.example.client.protocol.exception.WrongMessageTypeException;
import org.example.client.util.Images;
import org.w3c.dom.ls.LSOutput;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class BattleController {
    @FXML
    private Label hodLabel;
    @FXML
    private VBox healthBox;
    @FXML
    private GridPane gridPane;
    boolean hod;
    private Map<Integer, ProgressBar> map;
    private int choice = 0;

    public void initialize(){
        hod = BoardSingleton.getInstance().readCoordinateMessage(ClientImpl.getInstance().getLastMessage().getData()) == 1;
        AbstractEntity[][] board = BoardSingleton.getInstance().getBoard();
        for(int y = 0; y < gridPane.getColumnCount(); y++){
            for(int x = 0; x < gridPane.getRowCount(); x++){
                AbstractEntity entity = board[x][y];
                if(entity == null){
                    Pane pane = new Pane();
                    pane.setStyle("-fx-border-color: black; -fx-background-color: white");
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


                            AbstractSoldier soldier = (AbstractSoldier) entity;
                            if(BoardSingleton.getInstance().isMySoldier(soldier)){
                                pane.setStyle("-fx-background-color: blue");
                            }else{
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

        if(hod){
            hodLabel.setText("You are going");
        }
        else{
            hodLabel.setText("Opponent is going");
            MyService myService = new MyService();
            myService.setOnSucceeded((event1 -> {
                Message message = ClientImpl.getInstance().getLastMessage();
                System.out.println(Message.toString(message));
                if(message.getData().length == 3){
                    int index = message.getData()[0];
                    int column1 = message.getData()[1];
                    int row1 = message.getData()[2];
                    move(index, column1, row1);
                }

                myService.cancelWaiting();
                hod = true;
                hodLabel.setText("You are going");
                editBoardByDoingMovement();
            }));
            myService.start();
        }
    }

    private void addHealthBars(List<SoldierWithIndexAndCoordinats> soldiers) {
        for (SoldierWithIndexAndCoordinats soldier : soldiers){
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
            label.setOnMouseClicked((event -> {
                if(BoardSingleton.getInstance().isMySoldier(soldier.getIndex())){
                    if(choice == 0){
                        choice = soldier.getIndex();
                        editBoardByDoingMovementSet(soldier.getCol(), soldier.getRow(), soldier.getSoldier().getMovementradius(), "grey");
                    }
                    else{
                        if(choice != soldier.getIndex()){
                            SoldierWithIndexAndCoordinats tempSold = BoardSingleton.getInstance().getMySoldierByIndex(choice);
                            editBoardByDoingMovementSet(tempSold.getCol(), tempSold.getRow(), tempSold.getSoldier().getMovementradius(), "white");

                            choice = soldier.getIndex();
                            editBoardByDoingMovementSet(soldier.getCol(), soldier.getRow(), soldier.getSoldier().getMovementradius(), "grey");
                            return;
                        }
                        choice = 0;
                        editBoardByDoingMovementSet(soldier.getCol(), soldier.getRow(), soldier.getSoldier().getMovementradius(), "white");
                    }
                }
            }));
            label.setOnMouseEntered((event -> {
                if(!BoardSingleton.getInstance().isMySoldier(soldier.getIndex())){
                    editBoardByChoicingOpponent(soldier.getCol(),soldier.getRow(),true);
                }
            }));
            label.setOnMouseExited((event -> {
                if(!BoardSingleton.getInstance().isMySoldier(soldier.getIndex())){
                    editBoardByChoicingOpponent(soldier.getCol(),soldier.getRow(),false);
                }
            }));
            healthBox.getChildren().add(label);
            healthBox.getChildren().add(progressBar);
        }
    }

    private void editBoardByChoicingOpponent(int column, int row,boolean flag) {
        for(Node node : gridPane.getChildren()){
            if(Objects.equals(GridPane.getColumnIndex(node),column) && Objects.equals(GridPane.getRowIndex(node),row)){
                Pane pane = (Pane) node;
                if(flag) {
                    pane.setStyle("-fx-background-color: #5E0505FF");
                }else{
                    pane.setStyle("-fx-background-color: red");
                }
                break;
            }
        }
    }

    private void editBoardByDoingMovement(){
        List<Map.Entry<Integer, Integer>> mySoldiersCoordinates = BoardSingleton.getInstance().getMySoldiersCoordinates();
        for(Map.Entry<Integer, Integer> entry : mySoldiersCoordinates){
            Pane pane = (Pane) gridPane.getChildren().stream()
                    .filter((ent)-> Objects.equals(GridPane.getColumnIndex(ent), entry.getKey()) && Objects.equals(GridPane.getRowIndex(ent), entry.getValue())).findAny().orElseThrow();
            SoldierWithIndexAndCoordinats soldier = BoardSingleton.getInstance().getMySoldierByCoordinates(entry.getKey(), entry.getValue());
            pane.setOnMouseClicked((event ->
            {
                if(choice == 0){
                    choice = soldier.getIndex();

                    editBoardByDoingMovementSet(entry.getKey(), entry.getValue(), soldier.getSoldier().getMovementradius(), "grey");
                }
                else{
                    if(choice != soldier.getIndex()){return;}
                    choice = 0;
                    editBoardByDoingMovementSet(entry.getKey(), entry.getValue(), soldier.getSoldier().getMovementradius(), "white");
                }
            }));

        }
    }

    private void editBoardByDoingMovementSet(Integer column, Integer row, int movementradius, String color) {
        AbstractEntity[][] board = BoardSingleton.getInstance().getBoard();
        for(int tempColumn = column - movementradius; tempColumn <= column + movementradius; tempColumn++){
            if(tempColumn < 0 || tempColumn > 14) continue;
            for(int tempRow = row - movementradius; tempRow <= row + movementradius; tempRow++){
                if(tempRow < 0 || tempRow > 14) continue;
                if(board[tempRow][tempColumn] == null){
                    int finalTempColumn = tempColumn;
                    int finalTempRow = tempRow;
                    Pane pane = (Pane) gridPane.getChildren().stream()
                            .filter((ent)->Objects.equals(GridPane.getColumnIndex(ent),finalTempColumn) && Objects.equals(GridPane.getRowIndex(ent),finalTempRow)).findAny().orElseThrow();
                    pane.setStyle("-fx-background-color: %s; -fx-border-color: black".formatted(color));
                    //TODO onCLick
                    if(color.equals("grey")){
                        pane.setOnMouseClicked((event -> {
                            if(hod && choice != 0){
                                editBoardByDoingMovementSet(column,row,movementradius,"white");
                                move(choice,finalTempColumn,finalTempRow);
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
                                MyService myService = new MyService();
                                myService.setOnSucceeded((event1 -> {
                                    Message message = ClientImpl.getInstance().getLastMessage();
                                    if(message.getData().length == 3){
                                        int index = message.getData()[0];
                                        int column1 = message.getData()[1];
                                        int row1 = message.getData()[2];
                                        move(index, column1, row1);
                                    }

                                    myService.cancelWaiting();
                                    hod = true;
                                    hodLabel.setText("You are going");
                                    editBoardByDoingMovement();
                                }));
                                myService.start();
                            }
                        }));
                    }
                }
            }
        }
    }


    private void move(int index, int column1, int row1){

        SoldierWithIndexAndCoordinats sold = BoardSingleton.getInstance().getSoldierByIndex(index);
        int soldRow = sold.getRow();
        int soldCol = sold.getCol();
        BoardSingleton.getInstance().move(index, column1, row1);

        Pane soldier1 = (Pane) gridPane.getChildren().stream()
                .filter((ent)->Objects.equals(GridPane.getColumnIndex(ent), soldCol)
                        && Objects.equals(GridPane.getRowIndex(ent),soldRow)).findAny().orElseThrow();

        Pane temp1 = (Pane) gridPane.getChildren().stream()
                .filter((ent)->Objects.equals(GridPane.getColumnIndex(ent), column1)
                        && Objects.equals(GridPane.getRowIndex(ent),row1)).findAny().orElseThrow();

        gridPane.getChildren().remove(soldier1);
        gridPane.getChildren().remove(temp1);

        gridPane.add(temp1, soldCol, soldRow);

        gridPane.add(soldier1, column1, row1);
    }

    private String nameSoldier(AbstractSoldier soldier){
        if(soldier instanceof HeavyKnight){
            return "Heavy knight";
        }
        if(soldier instanceof Archer){
            return "Archer";
        }
        if(soldier instanceof Hiller){
            return "Hiller";
        }
        if(soldier instanceof HorseKnight){
            return "Horse knight";
        }
        return "WTF";
    }

    private class MyService extends Service<Boolean> {

        @Override
        protected Task<Boolean> createTask() {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return new Task<Boolean>() {

                @Override
                protected Boolean call() throws Exception {
                    Message message = ClientImpl.getInstance().getMessage();

                    updateValue(true);
                    return true;
                }


            };
        }
        public void cancelWaiting(){
            this.cancel();
        }
    }
}
