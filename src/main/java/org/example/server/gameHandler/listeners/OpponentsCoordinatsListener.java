package org.example.server.gameHandler.listeners;

import lombok.Setter;
import org.example.GameEntities.fabrica.SoldierFabrica;
import org.example.GameEntities.soldiers.AbstractSoldier;
import org.example.protocol.Message;
import org.example.protocol.exception.ExceedingTheMaximumLengthException;
import org.example.protocol.exception.WrongMessageTypeException;

import java.util.*;

public class OpponentsCoordinatsListener extends AbstractGameListener{
    private Map<Integer, Map.Entry<Byte,Byte>> map = new HashMap<>();
    private int lastKey = 0;
    @Setter
    private int cntOfUnits = 6;

    @Override
    public void handle(int i, Message message) {
        if(map.size() < cntOfUnits) {
            byte[] arr = message.getData();
            for (int j = 2; j < arr.length; j += 3) {
                lastKey++;
                soldierMap.put(lastKey, SoldierFabrica.getSoldier(arr[j - 2]));
                map.put(lastKey, new AbstractMap.SimpleEntry<Byte, Byte>(arr[j - 1], arr[j]));
            }
        }else{
            byte[] array = new byte[1 + cntOfUnits / 2 + cntOfUnits * 2];
            array[0] = (byte) i;
            if(i == 1){
                for(int j = 1; j <= cntOfUnits / 2; j++){
                    array[j] = (byte) j;
                }
                int now = 1 + cntOfUnits / 2;
                for (int j = 1 + cntOfUnits / 2; j <= cntOfUnits; j += 1) {
                    array[now] = (byte) j; now++;
                    array[now] = (byte) soldierMap.get(j).getINDEX(); now++;
                    array[now] = map.get(j).getKey(); now++;
                    array[now] = map.get(j).getValue(); now++;
                }
                try {
                    server.sendMessage(opponentOne, Message.createMessage(Message.TYPE3, array));
                } catch (ExceedingTheMaximumLengthException e) {
                    throw new RuntimeException(e);
                } catch (WrongMessageTypeException e) {
                    throw new RuntimeException(e);
                }
            }
            else{
                for(int j = 1; j <= cntOfUnits / 2; j++){
                    array[j] = (byte) (j + (cntOfUnits / 2));
                }
                int now = 1 + cntOfUnits / 2;
                for (int j = 1; j <= cntOfUnits / 2; j += 1) {
                    array[now] = (byte) j; now++;
                    array[now] = (byte) soldierMap.get(j).getINDEX(); now++;
                    array[now] = map.get(j).getKey(); now++;
                    array[now] = map.get(j).getValue(); now++;
                }
                try {
                    server.sendMessage(opponentTwo, Message.createMessage(Message.TYPE3, array));
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
