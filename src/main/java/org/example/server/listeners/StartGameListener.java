package org.example.server.listeners;



import org.example.protocol.Message;
import org.example.server.gameHandler.GameHandlerImpl;

import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import static java.util.Map.Entry;

public class StartGameListener extends AbstractServerListener {
    private static final int TYPE = 2;
    @Override
    public void handle(Socket socket, Message message) {
        Map<String, List<Entry<Socket, Boolean>>> sockets = server.getSockets();
        for (Entry<String, List<Map.Entry<Socket, Boolean>>> entry : sockets.entrySet()) {
            for(Entry<Socket, Boolean> socketEntry : entry.getValue()) {
                if(socketEntry.getKey().isConnected() && socketEntry.getKey().equals(socket)) {
                    if (message.getData()[0] == 1) {
                        socketEntry.setValue(true);
                        if (entry.getValue().size() == 2) {
                            List<Map.Entry<Socket, Boolean>> values = entry.getValue();
                            boolean flag = true;
                            for (Map.Entry<Socket, Boolean> value : values) {
                                flag = flag && value.getValue();
                            }
                            if (flag) {
                                Thread t1 = new Thread(new GameHandlerImpl(
                                        values.get(0).getKey(),
                                        values.get(1).getKey(),
                                        server,
                                        message.getData()[1]
                                ));
                                t1.setDaemon(true);
                                t1.start();
                                server.getSockets().remove(entry.getKey());
                            }
                            break;
                        }
                    }else{
                        List<Entry<Socket, Boolean>> entries = server.getSockets().get(entry.getKey());
                        for(Entry<Socket,Boolean> ent : entries){
                            if(ent.getKey() == socket){
                                entries.remove(ent);
                                break;
                            }
                        }
                        if (server.getSockets().get(entry.getKey()).isEmpty()) {
                            server.getSockets().remove(entry.getKey());
                        }
                    }
                    break;
                }
            }
        }
    }

    @Override
    public int getType() {
        return TYPE;
    }
}
