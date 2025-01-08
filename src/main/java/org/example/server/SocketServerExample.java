package org.example.server;



import org.example.protocol.Message;
import org.example.server.listeners.ServerEventListener;
import org.w3c.dom.ls.LSOutput;

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
    private Map<String,List<Entry<Socket, Boolean>>> sockets;

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
        for(Map.Entry<String, List<Map.Entry<Socket, Boolean>>> entry : sockets.entrySet()){
            try{
                List<Map.Entry<Socket, Boolean>> twoSockets = entry.getValue();
                for(Map.Entry<Socket, Boolean> entry1 : twoSockets){
                    entry1.getKey().getOutputStream().write(Message.getBytes(message));
                    entry1.getKey().getOutputStream().flush();
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
               System.out.println(Message.toString(message));
               for(ServerEventListener listener : listeners){
                   if(listener.getType() == message.getType()){
                       listener.handle(socket, message);
                   }
               }
           }catch(Exception e){
               for(Map.Entry<String, List<Map. Entry<Socket, Boolean>>> entry : sockets.entrySet()){
                   for(Map.Entry<Socket, Boolean> socketEntry : entry.getValue()){
                       if(socketEntry.getKey() == socket){
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

    public Map<String, List<Map.Entry<Socket, Boolean>>> getSockets() {
        return sockets;
    }
}
