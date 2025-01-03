package org.example.server.gameHandler.listeners;

import org.example.protocol.Message;

import java.sql.SQLOutput;
import java.util.Arrays;

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

        try{
            if(i == 1){
                System.out.println("Я увидел сообщение первого человека: ");
                System.out.println(Arrays.toString(message.getData()));
                Message.readMessage(opponentTwo.getInputStream());
                System.out.println("Я прочитал сообщение второго человека");
                server.sendMessage(opponentTwo, message);
                System.out.println("Я отправил второму сообщение");
            }else{
                System.out.println("Я увидел сообщение второго человека");
                System.out.println(Arrays.toString(message.getData()));
                Message.readMessage(opponentOne.getInputStream());
                System.out.println("Я прочитал сообщение первого человека");
                server.sendMessage(opponentOne, message);
                System.out.println("Я отправил сообщение первому человеку");
            }
        }catch(Exception e){
            throw new RuntimeException(e);
        }

    }

    @Override
    public int getType() {
        return Message.TYPE4;
    }
}
