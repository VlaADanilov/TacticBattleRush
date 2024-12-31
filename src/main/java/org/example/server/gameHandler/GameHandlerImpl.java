package org.example.server.gameHandler;


import lombok.Getter;
import lombok.Setter;
import org.example.GameEntities.elements.AbstractElement;
import org.example.GameEntities.soldiers.AbstractSoldier;
import org.example.protocol.Message;
import org.example.server.ServerExample;
import org.example.server.gameHandler.listeners.AbstractGameListener;
import org.example.server.gameHandler.listeners.OpponentsCoordinatsListener;

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
    }

    private void sendStartMessage(Byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
        for(int i = 0; i < bytes.length; i++){
            buffer.put(bytes[i]);
        }
        try {
            server.sendMessage(opponentOne, Message.createMessage(2, buffer.array()));
            server.sendMessage(opponentTwo, Message.createMessage(2, buffer.array()));
        }catch (Exception e){
            throw new RuntimeException(e);
        }

    }

    @Override
    public void run() {
        readAndSendCoordinatesMessages();
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
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
