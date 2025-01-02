package org.example.server.gameHandler.listeners;

import org.example.GameEntities.fabrica.SoldierFabrica;
import org.example.GameEntities.soldiers.AbstractSoldier;
import org.example.protocol.Message;
import org.example.protocol.exception.ExceedingTheMaximumLengthException;
import org.example.protocol.exception.WrongMessageTypeException;

import java.util.*;

public class OpponentsCoordinatsListener extends AbstractGameListener{
    private Map<Integer, Map.Entry<Byte,Byte>> map = new HashMap<>();
    private int lastKey = 0;

    @Override
    public void handle(int i, Message message) {
        if(map.size() < 6) {
            byte[] arr = message.getData();
            for (int j = 0; j < arr.length; j += 3) {
                lastKey++;
                soldierMap.put(lastKey, SoldierFabrica.getSoldier(arr[j]));
                map.put(lastKey, new AbstractMap.SimpleEntry<Byte, Byte>(arr[j + 1], arr[j + 2]));
            }
        }else{
            byte[] array = new byte[16];
            array[0] = (byte) i;
            if(i == 1){
                array[1] = 1;
                array[2] = 2;
                array[3] = 3;
                int now = 4;
                for (int j = 4; j < 7; j += 1) {
                    array[now] = (byte) j; now++;
                    array[now] = (byte) soldierMap.get(j).getINDEX(); now++;
                    array[now] = map.get(j).getKey(); now++;
                    array[now] = map.get(j).getValue(); now++;
                }
                try {
                    server.sendMessage(opponentOne, Message.createMessage(3, array));
                } catch (ExceedingTheMaximumLengthException e) {
                    throw new RuntimeException(e);
                } catch (WrongMessageTypeException e) {
                    throw new RuntimeException(e);
                }
            }
            else{
                array[1] = 4;
                array[2] = 5;
                array[3] = 6;
                int now = 4;
                for (int j = 1; j < 4; j += 1) {
                    array[now] = (byte) j; now++;
                    array[now] = (byte) soldierMap.get(j).getINDEX(); now++;
                    array[now] = map.get(j).getKey(); now++;
                    array[now] = map.get(j).getValue(); now++;
                }
                try {
                    server.sendMessage(opponentTwo, Message.createMessage(3, array));
                } catch (ExceedingTheMaximumLengthException e) {
                    throw new RuntimeException(e);
                } catch (WrongMessageTypeException e) {
                    throw new RuntimeException(e);
                }
            }

        }
    }

    @Override
    public int getType() {
        return Message.TYPE3;
    }
}
