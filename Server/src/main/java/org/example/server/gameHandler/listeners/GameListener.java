package org.example.server.gameHandler.listeners;


import org.example.server.ServerExample;
import org.example.server.gameHandler.exception.PlayerException;
import ru.itis.prot.gameEntities.soldiers.AbstractSoldier;
import ru.itis.prot.protocol.Message;

import java.net.Socket;
import java.util.Map;

public interface GameListener {
    void init(ServerExample serverExample, Socket oneSocket, Socket twoSocket, Map<Integer, AbstractSoldier> soldierMap);
    void handle(int i, Message message) throws PlayerException;
    int getType();
}
