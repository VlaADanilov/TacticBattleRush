package org.example.server.gameHandler.listeners;

import org.example.protocol.Message;

public class UserActionMoveListener extends AbstractGameListener{
    @Override
    public void handle(int i, Message message) {
        if(message.getData().length == 3) {
            try {
                if (i == 1) {
                    Message.readMessage(opponentTwo.getInputStream());
                    server.sendMessage(opponentTwo, message);
                } else {
                    Message.readMessage(opponentOne.getInputStream());
                    server.sendMessage(opponentOne, message);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }else{
            throw new RuntimeException("WTF?!");
        }
    }

    @Override
    public int getType() {
        return Message.TYPE_MOVE;
    }
}
