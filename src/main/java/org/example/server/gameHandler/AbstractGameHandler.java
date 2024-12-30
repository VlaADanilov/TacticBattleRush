package org.example.server.gameHandler;

import org.example.GameEntities.AbstractEntity;
import org.example.GameEntities.fabrica.ElementFabrica;

import java.util.Random;

public abstract class AbstractGameHandler implements GameHandlerInterface{
    protected AbstractEntity[][] board;

    protected void initBoard(){
        board = new AbstractEntity[15][15];

        Random rand = new Random();
        for(int i = 0; i< rand.nextInt(13); i++){
            int x = rand.nextInt(15);
            int y = rand.nextInt(15);
            board[y][x] = ElementFabrica.getElement();
        }
    }
}
