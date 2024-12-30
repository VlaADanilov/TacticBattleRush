package org.example.client.board;

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

    public AbstractEntity[][] getBoard() {
        return board;
    }
}
