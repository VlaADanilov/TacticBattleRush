package org.example.client.connectors;


import org.example.client.protocol.Message;

public interface ClientExample {
    void connect();
    void sendMessage(Message message);
    Message getMessage();
}
