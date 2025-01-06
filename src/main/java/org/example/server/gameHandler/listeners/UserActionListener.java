package org.example.server.gameHandler.listeners;

import org.example.protocol.Message;

import java.sql.SQLOutput;
import java.util.Arrays;

public class UserActionListener extends AbstractGameListener{
    @Override
    public void handle(int i, Message message) {
        if(message.getData().length == 2 || message.getData().length == 5){

            int index1;
            int index2;

            if(message.getData().length == 2){
                index1 = message.getData()[0];
                index2 = message.getData()[1];
            }else{
                index1 = message.getData()[3];
                index2 = message.getData()[4];
            }

            action(index1, index2);
        }

        try{
            if(i == 1){
                Message.readMessage(opponentTwo.getInputStream());
                server.sendMessage(opponentTwo, message);
            }else{
                Message.readMessage(opponentOne.getInputStream());
                server.sendMessage(opponentOne, message);
            }
        }catch(Exception e){
            throw new RuntimeException(e);
        }

    }

    private void action(int index1, int index2){
        soldierMap.get(index1).action(soldierMap.get(index2));
        if(soldierMap.get(index2).getHealth() < 0) soldierMap.get(index2).setHealth(0);
    }

    @Override
    public int getType() {
        return Message.TYPE4;
    }
}
