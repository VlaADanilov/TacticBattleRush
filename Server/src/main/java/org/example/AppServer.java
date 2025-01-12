package org.example;

import org.example.server.SocketServerExample;
import org.example.server.listeners.AddToRoomListener;
import org.example.server.listeners.StartGameListener;

public class AppServer {
    private static final int PORT = 50000;

    public static void main(String[] args) {
        SocketServerExample server = new SocketServerExample(PORT);
        server.registerListener(new AddToRoomListener());
        server.registerListener(new StartGameListener());
        //TODO register listeners
        server.start();
    }
}
