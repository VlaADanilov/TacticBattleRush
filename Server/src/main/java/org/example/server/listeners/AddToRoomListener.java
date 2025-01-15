package org.example.server.listeners;

import org.example.utils.OnePlayerInRoom;
import ru.itis.prot.protocol.Message;

import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AddToRoomListener extends AbstractServerListener{
    private final static int TYPE = 1;

    @Override
    public void handle(Socket socket, Message message) {
        if(!init){
            throw new RuntimeException("Listener not initialized");
        }
        String room = new String(message.getData());
        String rezult = addToRoom(room, socket);
        ByteBuffer buf = ByteBuffer.allocate(rezult.getBytes().length).put(rezult.getBytes());
        try {
            server.sendMessage(socket, Message.createMessage(Message.TYPE1, buf.array()));
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }


    private String addToRoom(String roomId, Socket socket){
        Map<String, List<OnePlayerInRoom>> sockets = server.getSockets();
        if(sockets.get(roomId) == null || sockets.get(roomId).isEmpty()){
            List<OnePlayerInRoom> socketList = new ArrayList<>();
            socketList.add(new OnePlayerInRoom(socket));
            sockets.put(roomId, socketList);
            return roomId + " Ожидание";
        }else{
            if(sockets.get(roomId).size() == 2){
                return roomId + " Занято";
            }else{
                sockets.get(roomId).add(new OnePlayerInRoom(socket));
                return roomId + " Присоединился";
            }
        }
    }

    @Override
    public int getType() {
        return TYPE;
    }
}
