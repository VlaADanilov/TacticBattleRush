package ru.itis.prot.protocol;


import ru.itis.prot.protocol.exception.ExceedingTheMaximumLengthException;
import ru.itis.prot.protocol.exception.WrongMessageTypeException;
import ru.itis.prot.protocol.exception.FirstBytesNotEqualsException;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;

//Message contents: [FIRST BYTES (2), MESSAGE TYPE(4), DATA LENGTH(4), DATA(?)]
public class Message {
    public static final int TYPE0 = 0;
    public static final int TYPE1 = 1; // подключение к комнате и результат подключения
    public static final int TYPE2 = 2; // готовность и старт игры
    public static final int TYPE3 = 3; // координаты моих персонажей и координаты персонажей соперника
    public static final int TYPE_ATTACK = 4; // атака
    public static final int TYPE5 = 5; // результат игры
    public static final int TYPE_MOVE = 6; // передвижение
    public static final int TYPE_ATTACK_AND_MOVE = 7; // передвижение + атака


    private static final int MAX_LENGTH = 1000;

    protected static final byte[] START_BYTES = new byte[]{0xA, 0xB};

    protected final byte[] data;
    protected final int type;
    protected Message(int type, byte[] data){
        this.data = data;
        this.type = type;
    }

    public static Message createMessage(int messageType, byte[] data) throws ExceedingTheMaximumLengthException, WrongMessageTypeException {
        if(data.length > MAX_LENGTH){
            throw new ExceedingTheMaximumLengthException("Message can't be " + data.length
                    + " bytes length. Maximum is " + MAX_LENGTH + "."
            );
        }
        if(messageType != TYPE0 && messageType != TYPE1 && messageType != TYPE2
                && messageType != TYPE3 && messageType != TYPE_ATTACK
                && messageType != TYPE5 && messageType != TYPE_MOVE
                && messageType != TYPE_ATTACK_AND_MOVE){
            throw new WrongMessageTypeException("Wrong message type");
        }
        return new Message(messageType, data);
    }

    public static byte[] getBytes(Message message){
        int rawMessageLength = START_BYTES.length + 4 + 4 + message.getData().length;
        byte[] rawMessage = new byte[rawMessageLength];
        int j = 0;
        for(int i = 0; i < START_BYTES.length; i++){
            rawMessage[j++] = START_BYTES[i];
        }
        byte[] type = ByteBuffer.allocate(4).putInt(message.getType()).array();
        for(int i = 0; i < type.length; i++){
            rawMessage[j++] = type[i];
        }
        byte[] length = ByteBuffer.allocate(4).putInt(message.getData().length).array();
        for(int i = 0; i < length.length; i++){
            rawMessage[j++] = length[i];
        }
        byte[] data = message.getData();
        for(int i = 0; i < data.length; i++){
            rawMessage[j++] = data[i];
        }
        return rawMessage;
    }

    public byte[] getData() {
        return data;
    }

    public int getType() {
        return type;
    }

    public static String toString(Message message){
        StringBuilder sb = new StringBuilder();
        String delimeter = " ";
        String nl = System.getProperty("line.separator");
        byte[] bytes = Message.getBytes(message);
        sb.append("First bytes: ");
        for(int i = 0; i < START_BYTES.length; i++){
            sb.append(bytes[i]);sb.append(delimeter);
        }
        sb.append(nl);
        sb.append("Type: ");
        sb.append(ByteBuffer.wrap(bytes, 2, 4).getInt());
        sb.append(nl);
        sb.append("Length: ");
        sb.append(ByteBuffer.wrap(bytes, 6, 4).getInt());
        sb.append(nl);
        sb.append("Data: ");
        for(int i = 10; i < bytes.length; i++){
            sb.append(bytes[i]);
            sb.append(delimeter);
        }
        return sb.toString();
    }

    public static Message readMessage(InputStream in){
        byte[] buffer = new byte[MAX_LENGTH];// Not the most optimized approach
        try{
            synchronized (in) {
                in.read(buffer, 0, START_BYTES.length);//Block Thread here
                if (!Arrays.equals(
                        Arrays.copyOfRange(buffer, 0, START_BYTES.length),
                        START_BYTES)) {
                    throw new FirstBytesNotEqualsException(
                            "Message first bytes must be " + Arrays.toString(START_BYTES)
                    );
                }
                in.read(buffer, 0, 4);//Block Thread here
                int messageType = ByteBuffer.wrap(buffer, 0, 4).getInt();
                if (messageType != TYPE0 && messageType != TYPE1 && messageType != TYPE2
                        && messageType != TYPE3 && messageType != TYPE_ATTACK
                        && messageType != TYPE5 && messageType != TYPE_MOVE
                        && messageType != TYPE_ATTACK_AND_MOVE) {
                    throw new WrongMessageTypeException("Wrong message type: " + messageType + ".");
                }
                in.read(buffer, 0, 4);//Block Thread here
                int messageLength = ByteBuffer.wrap(buffer, 0, 4).getInt();
                if (messageLength > MAX_LENGTH) {
                    throw new ExceedingTheMaximumLengthException(
                            "Message can't be " + messageLength
                                    + " bytes length. Maximum is " + MAX_LENGTH + "."
                    );
                }
                in.read(buffer, 0, messageLength);//Can end before messageLength
                return new Message(messageType, Arrays.copyOfRange(buffer, 0, messageLength));
            }
        }
        catch(Exception e){
            throw new IllegalArgumentException("Can't read message", e);
        }

    }
}
