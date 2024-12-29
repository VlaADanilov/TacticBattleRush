package org.example.server.gameHandler.listeners;


import org.example.server.ServerExample;

public abstract class AbstractGameListener implements GameListener{
    protected boolean init;
    protected ServerExample server;

    @Override
    public void init(ServerExample server) {
        this.server = server;
        this.init = true;
    }
}
