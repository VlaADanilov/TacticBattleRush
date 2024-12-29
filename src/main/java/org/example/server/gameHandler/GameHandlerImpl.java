package org.example.server.gameHandler;


import org.example.server.ServerExample;
import org.example.server.gameHandler.listeners.AbstractGameListener;

import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class GameHandlerImpl extends AbstractGameHandler implements Runnable{
    private ServerExample server;
    private Socket opponentOne;
    private Socket opponentTwo;
    private List<AbstractGameListener> listeners;
    public int hod;


    public GameHandlerImpl(Socket opponentOne, Socket opponentTwo, ServerExample serverExample) {
    }

    @Override
    public void run() {
    }

    public Socket getOpponentOne() {
        return opponentOne;
    }

    public Socket getOpponentTwo() {
        return opponentTwo;
    }
}
