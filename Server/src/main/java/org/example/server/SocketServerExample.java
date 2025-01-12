package org.example.server;




import org.example.server.listeners.ServerEventListener;
import org.example.utils.OnePlayerInRoom;
import ru.itis.prot.protocol.Message;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static java.util.Map.Entry;

public class SocketServerExample implements ServerExample{
    private List<ServerEventListener> listeners;
    private int port;
    private ServerSocket serverSocket;
    private boolean started;
    private final Map<String, List<OnePlayerInRoom>> sockets;

    public SocketServerExample(int port) {
        this.listeners = new ArrayList<>();
        this.port = port;
        this.started = false;
        this.sockets = new HashMap<>();
    }

    @Override
    public void registerListener(ServerEventListener listener) {
        if(started){
            throw new RuntimeException("Server already started");
        }
        listener.init(this);
        listeners.add(listener);
    }

    @Override
    public void sendMessage(Socket socket, Message message) {
        if(!started){
            throw new RuntimeException("Server not started");
        }
        try{
            socket.getOutputStream().write(Message.getBytes(message));
            socket.getOutputStream().flush();
        }catch(IOException e){
            throw new RuntimeException("Cannot send message", e);
        }
    }

    @Override
    public void sendBroadCastMessage(Message message) {
        if(!started){
            throw new RuntimeException("Server not started");
        }
        for(Map.Entry<String, List<OnePlayerInRoom>> entry : sockets.entrySet()){
            try{
                List<OnePlayerInRoom> twoSockets = entry.getValue();
                for(OnePlayerInRoom player : twoSockets){
                    player.getSocket().getOutputStream().write(Message.getBytes(message));
                    player.getSocket().getOutputStream().flush();
                }
            }catch(IOException e){
                throw new RuntimeException("Cannot send message", e);
            }
        }
    }

    @Override
    public void start() {
        try{
            serverSocket = new ServerSocket(port);
            started = true;
        }catch(IOException e){
            throw new RuntimeException("Cannot start server", e);
        }
        try{
            while(true){
                Socket socket = serverSocket.accept();
                handleConnection(socket);
                //запустить Thread ожидания готовности
                waitReady(socket);
            }
        }catch(IOException e){
            throw new RuntimeException("Cannot connect to server", e);
        }
    }

    private void waitReady(Socket socket) {
        Thread thread = new Thread(() -> {
           try{
               Message message = Message.readMessage(socket.getInputStream());
               for(ServerEventListener listener : listeners){
                   if(listener.getType() == message.getType()){
                       listener.handle(socket, message);
                   }
               }
           }catch(Exception e){
               e.printStackTrace();
               for(Map.Entry<String, List<OnePlayerInRoom>> entry : sockets.entrySet()){
                   for(OnePlayerInRoom socketEntry : entry.getValue()){
                       if(socketEntry.getSocket() == socket){
                           sockets.get(entry.getKey()).remove(socketEntry);
                           if(sockets.get(entry.getKey()).isEmpty()){
                               sockets.remove(entry.getKey());
                           }
                       }
                   }
               }
           }
        });
        thread.setDaemon(true);
        thread.start();
    }

    private void handleConnection(Socket socket) {
        try{
            Message message = Message.readMessage(socket.getInputStream());
            for(ServerEventListener listener : listeners){
                if(message.getType() == listener.getType()){
                    listener.handle(socket, message);
                    break;
                }
            }
        }
        catch(IOException ex) {
            throw new RuntimeException("Cannot handle message", ex);
        }
    }

    public Map<String, List<OnePlayerInRoom>> getSockets() {
        return sockets;
    }
}
