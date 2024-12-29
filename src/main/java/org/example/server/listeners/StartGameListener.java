package org.example.server.listeners;



import org.example.protocol.Message;
import org.example.server.gameHandler.GameHandlerImpl;

import java.net.Socket;
import java.util.List;
import java.util.Map;

public class StartGameListener extends AbstractServerListener {
    private static final int TYPE = 2;
    @Override
    public void handle(Socket socket, Message message) {
        Map<String, List<Map.Entry<Socket, Boolean>>> sockets = server.getSockets();
        for (Map.Entry<String, List<Map.Entry<Socket, Boolean>>> entry : sockets.entrySet()) {
            for(Map.Entry<Socket, Boolean> socketEntry : entry.getValue()) {
                if(socketEntry.getKey().isConnected() && socketEntry.getKey().equals(socket)) {
                    socketEntry.setValue(true);
                    if(entry.getValue().size() == 2) {
                        List<Map.Entry<Socket, Boolean>> values = entry.getValue();
                        boolean flag = true;
                        for(Map.Entry<Socket, Boolean> value : values) {
                            flag = flag && value.getValue();
                        }
                        if(flag) {
                            Thread t1 = new Thread(new GameHandlerImpl(
                                    values.get(0).getKey(),
                                    values.get(1).getKey(),
                                    server
                            ));
                            t1.setDaemon(true);
                            t1.start();
                        }
                    }
                }
            }
        }
    }

    @Override
    public int getType() {
        return TYPE;
    }
}
