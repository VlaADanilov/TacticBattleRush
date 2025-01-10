package org.example.server.gameHandler.listeners;


import org.example.GameEntities.soldiers.AbstractSoldier;
import org.example.protocol.Message;
import org.example.server.ServerExample;
import org.example.server.gameHandler.exception.PlayerException;

import java.net.Socket;
import java.util.Map;

public interface GameListener {
    void init(ServerExample serverExample, Socket oneSocket, Socket twoSocket, Map<Integer, AbstractSoldier> soldierMap);
    void handle(int i, Message message) throws PlayerException;
    int getType();
}
