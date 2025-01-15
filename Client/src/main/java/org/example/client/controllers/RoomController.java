package org.example.client.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import org.example.client.connectors.ClientImpl;
import ru.itis.prot.protocol.Message;
import org.example.client.HelloApplication;
import org.example.client.service.MessageWaitingService;

import java.io.IOException;
import java.nio.ByteBuffer;


public class RoomController {
    @FXML
    private ComboBox<Byte> countOfUnitsComboBox;
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
            countOfUnitsComboBox.setDisable(true);
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
        Byte selectedValue = countOfUnitsComboBox.getValue();
        if(selectedValue == null) selectedValue = 0;
        ByteBuffer buffer = ByteBuffer.allocate(2 + room_number.getText().getBytes().length);
        buffer.put((byte) 1);
        buffer.put(selectedValue);
        buffer.put(room_number.getText().getBytes());
        try {
            client.sendMessage(Message.createMessage(Message.TYPE2, buffer.array()));
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
                client.sendMessage(Message.createMessage(Message.TYPE2, new byte[]{2}));
            }
            ClientImpl.getInstance().disconnect();
            HelloApplication.changeScene("hello-view.fxml");
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }
}
