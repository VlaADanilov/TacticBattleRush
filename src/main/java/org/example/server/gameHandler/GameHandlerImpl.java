package org.example.server.gameHandler;


import lombok.Getter;
import lombok.Setter;
import org.example.GameEntities.elements.AbstractElement;
import org.example.protocol.Message;
import org.example.server.ServerExample;
import org.example.server.gameHandler.listeners.AbstractGameListener;

import java.io.InputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class GameHandlerImpl extends AbstractGameHandler implements Runnable{
    private ServerExample server;
    private Socket opponentOne;
    private Socket opponentTwo;
    private List<AbstractGameListener> listeners;
    private int hod;


    public GameHandlerImpl(Socket opponentOne, Socket opponentTwo, ServerExample serverExample) {
        this.opponentOne = opponentOne;
        this.opponentTwo = opponentTwo;
        this.server = serverExample;
        hod = 1;

        initBoard();

        sendStartMessage();

        listeners = new ArrayList<>();
    }

    private void sendStartMessage() {
        Byte[] bytes = new Byte[60];
        int count = 0;
        for (byte y = 0; y < 15; y++) {
            for (byte x = 0; x < 15; x++) {
                if(board[y][x] != null){
                    bytes[count] = y;
                    count++;
                    bytes[count] = x;
                    count++;
                    bytes[count] = (byte)((AbstractElement) board[y][x]).getIndex();
                    count++;
                }
            }
        }
        ByteBuffer buffer = ByteBuffer.allocate(count - 1);
        for(int i = 0; i < count; i++){
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

    }

}
