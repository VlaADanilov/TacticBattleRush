package org.example.server.gameHandler.listeners;

import org.example.server.gameHandler.exception.PlayerException;
import ru.itis.prot.protocol.Message;

public class UserActionMoveAndAttackListener extends AbstractGameListener{
    @Override
    public void handle(int i, Message message) throws PlayerException {
        if(message.getData().length == 5){
            int index1 = message.getData()[3];
            int index2 = message.getData()[4];
            action(index1, index2);
        }
        if(isContinue()) {
            try {
                if (i == 1) {
                    Message.readMessage(opponentTwo.getInputStream());
                    server.sendMessage(opponentTwo, message);
                } else {
                    Message.readMessage(opponentOne.getInputStream());
                    server.sendMessage(opponentOne, message);
                }
            } catch (Exception e) {
                throw new PlayerException(i == 1 ? 2 : 1);
            }
        }
    }

    private void action(int index1, int index2){
        soldierMap.get(index1).action(soldierMap.get(index2));
        if(soldierMap.get(index2).getHealth() < 0) soldierMap.get(index2).setHealth(0);
    }

    private boolean isContinue(){
        int health = 0;
        for(int i = 1; i <= soldierMap.size() / 2; i++){
            health += soldierMap.get(i).getHealth();
        }
        if(health <= 0) return false;
        health = 0;
        for(int i = soldierMap.size() / 2 + 1; i <= soldierMap.size() ; i++){
            health += soldierMap.get(i).getHealth();
        }
        return !(health <= 0);
    }

    @Override
    public int getType() {
        return Message.TYPE_ATTACK_AND_MOVE;
    }
}
