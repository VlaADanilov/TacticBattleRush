package org.example.server.gameHandler.listeners;

import org.example.protocol.Message;

public class UserActionListener extends AbstractGameListener{
    @Override
    public void handle(int i, Message message) {
        //TODO делаю пока только для движения
        if(message.getData().length == 2){
            int index1 = message.getData()[0];
            int index2 = message.getData()[1];

            soldierMap.get(index1).action(soldierMap.get(index2));
            if(soldierMap.get(index2).getHealth() < 0) soldierMap.get(index2).setHealth(0);
        }


        if(i == 1){
            server.sendMessage(opponentTwo, message);
        }else{
            server.sendMessage(opponentOne, message);
        }
    }

    @Override
    public int getType() {
        return Message.TYPE4;
    }
}
