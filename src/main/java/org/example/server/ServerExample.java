package org.example.server;


import org.example.protocol.Message;
import org.example.server.listeners.ServerEventListener;

import java.net.Socket;
import java.util.List;
import java.util.Map;

public interface ServerExample {
    void registerListener(ServerEventListener listener);
    void sendMessage(Socket socket, Message message);
    void sendBroadCastMessage(Message message);
    void start();
    Map<String, List<Map.Entry<Socket, Boolean>>> getSockets();
}
