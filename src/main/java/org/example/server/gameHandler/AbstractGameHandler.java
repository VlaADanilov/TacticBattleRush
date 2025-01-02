package org.example.server.gameHandler;

import org.example.GameEntities.AbstractEntity;
import org.example.GameEntities.fabrica.ElementFabrica;

import java.util.Random;

public abstract class AbstractGameHandler implements GameHandlerInterface{
    protected Byte[] initBoard(){

        Random rand = new Random();
        int size = rand.nextInt(25);
        Byte[] bytes = new Byte[size*3];
        int index = 0;
        for(int i = 0; i< size; i++){
            int x = rand.nextInt(15);
            int y = rand.nextInt(15);
            bytes[index] = (byte)y;
            index++;
            bytes[index] = (byte)x;
            index++;
            bytes[index] = (byte) ElementFabrica.getElement().getIndex();
            index++;
        }
        return bytes;
    }
}
