package org.example.client.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.example.client.connectors.ClientImpl;
import org.example.client.protocol.Message;
import org.example.client.HelloApplication;
import org.example.client.service.MessageWaitingService;

import java.io.IOException;

public class RoomController {


    private MessageWaitingService service;
    @FXML
    private Label status;
    @FXML
    private Label room_number;

    @FXML
    private void initialize() throws IOException {
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
            HelloApplication.changeScene("hello-view.fxml");
            return;
        }
        try {
            client.sendMessage(Message.createMessage(2, new byte[0]));
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
        waitMessage();
    }

    private void waitMessage() {
        service = new MessageWaitingService();
        service.setOnSucceeded(event -> {
            try {
                service.cancelWaiting();
                service.cancel();
                HelloApplication.changeScene("setShips.fxml");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        service.start();
    }
}
