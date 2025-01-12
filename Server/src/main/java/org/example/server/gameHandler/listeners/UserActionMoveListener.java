package org.example.server.gameHandler.listeners;


import org.example.server.gameHandler.exception.PlayerException;
import ru.itis.prot.protocol.Message;

public class UserActionMoveListener extends AbstractGameListener{
    @Override
    public void handle(int i, Message message) throws PlayerException {
        if(message.getData().length == 3) {
            try {
                if (i == 1) {
                    Message.readMessage(opponentTwo.getInputStream());
                    server.sendMessage(opponentTwo, message);
                } else {
                    Message.readMessage(opponentOne.getInputStream());
                    server.sendMessage(opponentOne, message);
                }
            } catch(Exception e){
                throw new PlayerException(i == 1? 2 : 1);
            }
        }else{
            throw new PlayerException(i);
        }
    }

    @Override
    public int getType() {
        return Message.TYPE_MOVE;
    }
}
