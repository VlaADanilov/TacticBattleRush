package org.example.client.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import org.example.client.HelloApplication;
import org.example.client.connectors.ClientExample;
import org.example.client.connectors.ClientImpl;
import ru.itis.prot.protocol.Message;
import ru.itis.prot.protocol.exception.*;

import java.io.IOException;
import java.net.InetAddress;

public class HelloController {
    @FXML
    private TextField room;

    private ClientExample client;

    @FXML
    protected void startGame(ActionEvent actionEvent) throws IOException, ExceedingTheMaximumLengthException, WrongMessageTypeException {
        String text = room.getText();
        if(!text.isEmpty()){
            ClientImpl.init(InetAddress.getByName("127.0.0.1"), 50000);
            ClientImpl client = ClientImpl.getInstance();
            client.connect();
            Message message = Message.createMessage(Message.TYPE1, text.getBytes());
            client.sendMessage(message);
            client.getMessage();
            HelloApplication.changeScene("room.fxml");
        }
    }



    public ClientExample getClient() {
        return client;
    }

    public void setClient(ClientExample client) {
        this.client = client;
    }
}