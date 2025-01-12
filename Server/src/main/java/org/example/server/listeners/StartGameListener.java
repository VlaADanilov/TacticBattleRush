package org.example.server.listeners;



import org.example.server.gameHandler.GameHandlerImpl;
import org.example.utils.OnePlayerInRoom;
import ru.itis.prot.protocol.Message;

import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static java.util.Map.Entry;

public class StartGameListener extends AbstractServerListener {
    private static final int TYPE = 2;
    @Override
    public void handle(Socket socket, Message message) {
        Map<String, List<OnePlayerInRoom>> sockets = server.getSockets();

        String roomNumber = getRoomNumber(message.getData());

        List<OnePlayerInRoom> players = sockets.get(roomNumber);

        for(OnePlayerInRoom onePlayerInRoom : players) {
            if(onePlayerInRoom.getSocket() == socket && onePlayerInRoom.getSocket().isConnected()){
                if(message.getData()[0] == 1) {
                    onePlayerInRoom.setReady(true);
                    onePlayerInRoom.setCountOfUnits(message.getData()[1]);
                }else{
                    players.remove(onePlayerInRoom);
                    if(sockets.get(roomNumber).isEmpty()){
                        sockets.remove(roomNumber);
                    }
                }
            }
        }

        if(players.size() == 2) {
            boolean flag = true;
            for (OnePlayerInRoom value : players) {
                flag = flag && value.isReady();
            }
            if(flag) {
                Thread t1 = new Thread(new GameHandlerImpl(
                        players.get(0).getSocket(),
                        players.get(1).getSocket(),
                        server,
                        choice(players.get(0).getCountOfUnits(), players.get(1).getCountOfUnits())
                ));
                t1.setDaemon(true);
                t1.start();
                server.getSockets().remove(roomNumber);
            }
        }
    }

    private String getRoomNumber(byte[] array){
        byte[] newArray = new byte[array.length - 2];
        System.arraycopy(array, 2, newArray, 0, newArray.length);
        return new String(newArray);
    }

    private int choice(int x, int y){
        if(x == 0 && y != 0){
            return y;
        }
        if(x != 0 && y == 0){
            return x;
        }
        Random r = new Random();
        if(r.nextBoolean()){
            return y;
        }
        return x;
    }

    @Override
    public int getType() {
        return TYPE;
    }
}
