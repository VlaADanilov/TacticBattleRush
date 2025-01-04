package org.example.server.gameHandler;


import lombok.Getter;
import lombok.Setter;
import org.example.GameEntities.elements.AbstractElement;
import org.example.GameEntities.soldiers.AbstractSoldier;
import org.example.protocol.Message;
import org.example.server.ServerExample;
import org.example.server.gameHandler.listeners.AbstractGameListener;
import org.example.server.gameHandler.listeners.OpponentsCoordinatsListener;
import org.example.server.gameHandler.listeners.UserActionListener;

import java.io.IOException;
import java.io.InputStream;
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


    public GameHandlerImpl(Socket opponentOne, Socket opponentTwo, ServerExample serverExample) {
        this.opponentOne = opponentOne;
        this.opponentTwo = opponentTwo;
        this.server = serverExample;
        hod = 1;

        Byte[] elementsCoordinats = initBoard();

        sendStartMessage(elementsCoordinats);

        listeners = new ArrayList<>();
        AbstractGameListener opponentsCoordinatsListener = new OpponentsCoordinatsListener();
        opponentsCoordinatsListener.init(server, this.opponentOne, this.opponentTwo, this.soldierMap);
        listeners.add(opponentsCoordinatsListener);

        AbstractGameListener userActionListener = new UserActionListener();
        userActionListener.init(server, this.opponentOne, this.opponentTwo, this.soldierMap);
        listeners.add(userActionListener);
    }

    private void sendStartMessage(Byte[] bytes) {
        ByteBuffer buffer1 = ByteBuffer.allocate(bytes.length + 1);
        ByteBuffer buffer2 = ByteBuffer.allocate(bytes.length + 1);
        buffer1.put(Byte.parseByte("1"));
        buffer2.put(Byte.parseByte("2"));
        for(int i = 0; i < bytes.length; i++){
            buffer1.put(bytes[i]);
            buffer2.put(bytes[i]);
        }
        try {
            server.sendMessage(opponentOne, Message.createMessage(2, buffer1.array()));
            server.sendMessage(opponentTwo, Message.createMessage(2, buffer2.array()));
        }catch (Exception e){
            throw new RuntimeException(e);
        }

    }

    @Override
    public void run() {
        readAndSendCoordinatesMessages();
        int winner = 0;
        while(winner == 0){
            try {
                Message message;
                if(hod == 1){
                    message = Message.readMessage(opponentOne.getInputStream());
                }else{
                    message = Message.readMessage(opponentTwo.getInputStream());
                }
                for(AbstractGameListener listener : listeners){
                    if(listener.getType() == message.getType()){
                        listener.handle(hod, message);
                        break;
                    }
                }
                hod = hod == 1? 2 : 1;
                winner = whoWinner();
            }catch (Exception e){
                throw new RuntimeException(e);
            }
        }
        try {
            if (winner == 1) {
                server.sendMessage(opponentOne, Message.createMessage(5, new byte[]{1}));
                server.sendMessage(opponentTwo, Message.createMessage(5, new byte[]{2}));
            } else {
                server.sendMessage(opponentTwo, Message.createMessage(5, new byte[]{1}));
                server.sendMessage(opponentOne, Message.createMessage(5, new byte[]{2}));
            }
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    private void readAndSendCoordinatesMessages(){
        try {
            Message messageOne = Message.readMessage(opponentOne.getInputStream());
            Message messageTwo = Message.readMessage(opponentTwo.getInputStream());
            for(AbstractGameListener listener : listeners){
                if(listener.getType() == messageOne.getType() && listener.getType() == messageTwo.getType()){
                    listener.handle(1, messageOne);
                    listener.handle(2, messageTwo);
                    listener.handle(1, messageOne);
                    listener.handle(2, messageTwo);
                    break;
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
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
}
