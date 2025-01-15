package org.example.client.connectors;


import ru.itis.prot.protocol.Message;

public interface ClientExample {
    void connect();
    void sendMessage(Message message);
    Message getMessage();

    void disconnect();
}
