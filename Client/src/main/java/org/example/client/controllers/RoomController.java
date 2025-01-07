package org.example.client.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import org.example.client.connectors.ClientImpl;
import org.example.client.protocol.Message;
import org.example.client.HelloApplication;
import org.example.client.service.MessageWaitingService;

import java.io.IOException;

public class RoomController {
    @FXML
    private Button exitButton;
    @FXML
    private Button readyButton;
    private MessageWaitingService service;
    @FXML
    private Label status;
    @FXML
    private Label room_number;

    private boolean messageNeeded;

    @FXML
    private void initialize() throws IOException {
        messageNeeded = true;
        ClientImpl client = ClientImpl.getInstance();
        Message message = client.getLastMessage();
        String string = new String(message.getData());
        String stat = "";
        if(string.split(" ").length == 2){
            if(message.getType() == 1){
                String[] strings = new String(message.getData()).split(" ");
                room_number.setText(strings[0]);
                status.setText(strings[1]);
                stat = strings[1];
            }
        }
        if(stat.equals("Занято")){
            readyButton.setDisable(true);
            messageNeeded = false;
        }
    }

    private void waitMessage() {
        service = new MessageWaitingService();
        service.setOnSucceeded(event -> {
            try {
                service.cancelWaiting();
                service.cancel();
                HelloApplication.changeScene("setUnits.fxml");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        service.start();
    }

    public void ready(MouseEvent mouseEvent) {
        ClientImpl client = ClientImpl.getInstance();
        try {
            client.sendMessage(Message.createMessage(2, new byte[]{1}));
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
        waitMessage();
        exitButton.setDisable(true);
        readyButton.setDisable(true);
    }

    public void exit(MouseEvent mouseEvent) {
        ClientImpl client = ClientImpl.getInstance();
        try {
            if(messageNeeded) {
                client.sendMessage(Message.createMessage(2, new byte[]{2}));
            }
            ClientImpl.getInstance().disconnect();
            HelloApplication.changeScene("hello-view.fxml");
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }
}
