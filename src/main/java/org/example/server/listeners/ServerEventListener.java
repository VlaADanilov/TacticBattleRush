package org.example.server.listeners;



import org.example.protocol.Message;
import org.example.server.ServerExample;

import java.net.Socket;


public interface ServerEventListener {
    void init(ServerExample serverExample);
    void handle(Socket socket, Message message);
    int getType();
}
