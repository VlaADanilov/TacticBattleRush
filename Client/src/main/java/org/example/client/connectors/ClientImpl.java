package org.example.client.connectors;




import ru.itis.prot.protocol.Message;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class ClientImpl implements ClientExample{
    private static ClientImpl instance;
    protected  InetAddress address;
    protected int port;
    protected Socket socket;
    private Message lastMessage;


    private ClientImpl(InetAddress address, int port){
        this.address = address;
        this.port = port;
    };

    public InetAddress getAddress() {
        return address;
    }

    public static void init(InetAddress address, int port){
        instance = new ClientImpl(address, port);
    }

    public static ClientImpl getInstance(){
        return instance;
    }

    @Override
    public void connect(){
        try{
            socket = new Socket(address, port);
        }
        catch(IOException ex){
            throw new RuntimeException("Can't connect.", ex);
        }
    }

    @Override
    public void sendMessage(Message message){
        try{
            socket.getOutputStream().write(Message.getBytes(message));
            socket.getOutputStream().flush();
        }
        catch(IOException ex){
            throw new RuntimeException("Can't send message.", ex);
        }
    }

    @Override
    public Message getMessage() {
        try {
            lastMessage = Message.readMessage(socket.getInputStream());
            return lastMessage;
        }catch(IOException ex){
            throw new RuntimeException("Can't get message.", ex);
        }
    }

    @Override
    public void disconnect() {
        instance = null;
    }

    public Message getLastMessage() {
        return lastMessage;
    }


}
