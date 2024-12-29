package org.example.server.listeners;


import org.example.server.ServerExample;

public abstract class AbstractServerListener implements ServerEventListener{
    protected boolean init;
    protected ServerExample server;

    @Override
    public void init(ServerExample server) {
        this.server = server;
        this.init = true;
    }
}
