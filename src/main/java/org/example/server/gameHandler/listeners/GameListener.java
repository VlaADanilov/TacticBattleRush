package org.example.server.gameHandler.listeners;


import org.example.protocol.Message;
import org.example.server.ServerExample;

public interface GameListener {
    void init(ServerExample serverExample);
    void handle(int i, Message message);
    int getType();
}
