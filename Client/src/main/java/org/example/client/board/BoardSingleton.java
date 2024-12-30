package org.example.client.board;

import org.example.client.GameEntities.soldiers.AbstractSoldier;
import org.example.client.GameEntities.AbstractEntity;
import org.example.client.GameEntities.elements.AbstractElement;

public class BoardSingleton {
    private AbstractEntity[][] board;

    private static BoardSingleton instance;
    private BoardSingleton() {
        board = new AbstractEntity[15][15];
    }
    public synchronized static BoardSingleton getInstance() {
        if (instance == null) {
            instance = new BoardSingleton();
        }
        return instance;
    }

    public void addElement(AbstractElement element, int y, int x) {
        board[y][x] = element;
    }

    public void addMySoldier(AbstractSoldier soldier, int y, int x) {
        board[x][y] = soldier;
    }

    public void removeMySoldier(int y, int x){
        board[y][x] = null;
    }

    public boolean checkForNull(int y, int x){
        return board[x][y] == null;
    }

    public AbstractEntity[][] getBoard() {
        return board;
    }
}
