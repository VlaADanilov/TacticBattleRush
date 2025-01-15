package org.example.server;



import org.example.server.listeners.ServerEventListener;
import org.example.utils.OnePlayerInRoom;
import ru.itis.prot.protocol.Message;

import java.net.Socket;
import java.util.List;
import java.util.Map;

public interface ServerExample {
    void registerListener(ServerEventListener listener);
    void sendMessage(Socket socket, Message message);
    void sendBroadCastMessage(Message message);
    void start();
    Map<String, List<OnePlayerInRoom>> getSockets();
}
