package org.example.server.gameHandler;


import lombok.Getter;
import lombok.Setter;

import org.example.server.ServerExample;
import org.example.server.gameHandler.exception.PlayerException;
import org.example.server.gameHandler.listeners.*;
import ru.itis.prot.gameEntities.soldiers.AbstractSoldier;
import ru.itis.prot.protocol.Message;

import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class GameHandlerImpl extends AbstractGameHandler implements Runnable{
    private ServerExample server;
    private Socket opponentOne;
    private Socket opponentTwo;
    private List<AbstractGameListener> listeners;
    private int hod;
    private Map<Integer, AbstractSoldier> soldierMap = new HashMap<>();
    private int cntOfUnits = 3;
    private final static int BOARD_ROWS = 15;
    private final static int BOARD_COLS = 15;


    public GameHandlerImpl(Socket opponentOne, Socket opponentTwo, ServerExample serverExample, int cntOfUnits) {
        this.opponentOne = opponentOne;
        this.opponentTwo = opponentTwo;
        this.server = serverExample;
        hod = 1;

        Byte[] elementsCoordinats = initBoard(BOARD_ROWS,BOARD_COLS);

        listeners = new ArrayList<>();
        OpponentsCoordinatsListener opponentsCoordinatsListener = new OpponentsCoordinatsListener();
        opponentsCoordinatsListener.init(server, this.opponentOne, this.opponentTwo, this.soldierMap);
        listeners.add(opponentsCoordinatsListener);
        if(cntOfUnits != 0) {
            opponentsCoordinatsListener.setCntOfUnits(cntOfUnits * 2);
            this.cntOfUnits = cntOfUnits;
        }

        AbstractGameListener userActionAttackListenerListener = new UserActionAttackListener();
        userActionAttackListenerListener.init(server, this.opponentOne, this.opponentTwo, this.soldierMap);
        listeners.add(userActionAttackListenerListener);

        AbstractGameListener userActionMoveListenerListener = new UserActionMoveListener();
        userActionMoveListenerListener.init(server, this.opponentOne, this.opponentTwo, this.soldierMap);
        listeners.add(userActionMoveListenerListener);

        AbstractGameListener userActionMoveAndAttackListenerListener = new UserActionMoveAndAttackListener();
        userActionMoveAndAttackListenerListener.init(server, this.opponentOne, this.opponentTwo, this.soldierMap);
        listeners.add(userActionMoveAndAttackListenerListener);

        try {
            sendStartMessage(elementsCoordinats);
        }catch (PlayerException e){
            catchingException(e.getPlayer());
        }
    }

    private void sendStartMessage(Byte[] bytes) throws PlayerException {
        ByteBuffer buffer1 = ByteBuffer.allocate(bytes.length + 2);
        ByteBuffer buffer2 = ByteBuffer.allocate(bytes.length + 2);
        buffer1.put(Byte.parseByte("1"));
        buffer2.put(Byte.parseByte("2"));
        buffer1.put((byte) cntOfUnits);
        buffer2.put((byte) cntOfUnits);
        for (Byte aByte : bytes) {
            buffer1.put(aByte);
            buffer2.put(aByte);
        }
        try {
            server.sendMessage(opponentOne, Message.createMessage(Message.TYPE2, buffer1.array()));
        }catch (Exception e){
            throw new PlayerException(1);
        }
        try{
            server.sendMessage(opponentTwo, Message.createMessage(Message.TYPE2, buffer2.array()));
        }catch (Exception e){
            throw new PlayerException(2);
        }
    }

    @Override
    public void run() {
        try {
            readAndSendCoordinatesMessages();
        }catch (PlayerException e){
            catchingException(e.getPlayer());
        }
        int winner = 0;
        while(winner == 0){
            try {
                Message message;
                try {
                    if (hod == 1) {
                        message = Message.readMessage(opponentOne.getInputStream());
                    } else {
                        message = Message.readMessage(opponentTwo.getInputStream());
                    }
                }catch (Exception e){
                    throw new PlayerException(hod);
                }
                boolean flag = false;
                for(AbstractGameListener listener : listeners){
                    if(listener.getType() == message.getType()){
                        listener.handle(hod, message);
                        flag = true;
                        break;
                    }
                }
                if(!flag){
                    throw new PlayerException(hod);
                }
                hod = hod == 1? 2 : 1;
                winner = whoWinner();
            }catch (PlayerException e){
                catchingException(e.getPlayer());
            }
        }
        try {
            if (winner == 1) {
                server.sendMessage(opponentOne, Message.createMessage(Message.TYPE5, new byte[]{1}));
                server.sendMessage(opponentTwo, Message.createMessage(Message.TYPE5, new byte[]{2}));
            } else {
                server.sendMessage(opponentTwo, Message.createMessage(Message.TYPE5, new byte[]{1}));
                server.sendMessage(opponentOne, Message.createMessage(Message.TYPE5, new byte[]{2}));
            }
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    private void readAndSendCoordinatesMessages() throws PlayerException {
        Message messageOne, messageTwo;
        try {
            messageOne = Message.readMessage(opponentOne.getInputStream());
        }catch (Exception e){
            throw new PlayerException(1);
        }
        try {
            messageTwo = Message.readMessage(opponentTwo.getInputStream());
        }catch (Exception e){
            throw new PlayerException(2);
        }
        for(AbstractGameListener listener : listeners){
            if(listener.getType() == messageOne.getType() && listener.getType() == messageTwo.getType()){
                listener.handle(1, messageOne);
                listener.handle(2, messageTwo);
                listener.handle(1, messageOne);
                listener.handle(2, messageTwo);
                break;
            }
        }
    }

    private int whoWinner(){
        int summaHealth = 0;
        for(int i = 1; i <= soldierMap.size() / 2; i++){
            summaHealth += soldierMap.get(i).getHealth();
        }
        if(summaHealth == 0) return 2;
        summaHealth = 0;
        for(int i = soldierMap.size() / 2 + 1; i <= soldierMap.size(); i++){
            summaHealth += soldierMap.get(i).getHealth();
        }
        if(summaHealth == 0) return 1;

        return 0;
    }

    private void catchingException(int i){
        try {
            if (i == 1) {
                server.sendMessage(opponentTwo, Message.createMessage(Message.TYPE5, new byte[]{1}));
            } else {
                server.sendMessage(opponentOne, Message.createMessage(Message.TYPE5, new byte[]{1}));
            }
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
