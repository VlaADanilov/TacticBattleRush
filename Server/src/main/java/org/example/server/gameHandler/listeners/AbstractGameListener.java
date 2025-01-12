package org.example.server.gameHandler.listeners;



import org.example.server.ServerExample;
import ru.itis.prot.gameEntities.soldiers.AbstractSoldier;

import java.net.Socket;
import java.util.Map;

public abstract class AbstractGameListener implements GameListener{
    protected boolean init;
    protected ServerExample server;
    protected Socket opponentOne;
    protected Socket opponentTwo;
    protected Map<Integer, AbstractSoldier> soldierMap;

    @Override
    public void init(ServerExample server, Socket opponentOne, Socket opponentTwo, Map<Integer, AbstractSoldier> soldierMap) {
        this.server = server;
        this.init = true;
        this.opponentOne = opponentOne;
        this.opponentTwo = opponentTwo;
        this.soldierMap = soldierMap;
    }
}
