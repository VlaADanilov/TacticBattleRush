package org.example.server.listeners;




import org.example.server.ServerExample;
import ru.itis.prot.protocol.Message;

import java.net.Socket;


public interface ServerEventListener {
    void init(ServerExample serverExample);
    void handle(Socket socket, Message message);
    int getType();
}
