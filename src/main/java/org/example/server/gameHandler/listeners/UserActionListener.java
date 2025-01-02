package org.example.server.gameHandler.listeners;

import org.example.protocol.Message;

public class UserActionListener extends AbstractGameListener{
    @Override
    public void handle(int i, Message message) {
        //TODO делаю пока только для движения
        if(message.getData().length == 3){
            if(i == 1){
                server.sendMessage(opponentTwo, message);
            }else{
                server.sendMessage(opponentOne, message);
            }
        }
    }

    @Override
    public int getType() {
        return Message.TYPE4;
    }
}
