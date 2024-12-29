package org.example.server.gameHandler;

public abstract class AbstractGameHandler implements GameHandlerInterface{
    protected int[][] opponentOneBoard;
    protected int[][] opponentTwoBoard;

    protected void initializeBoards(){
        opponentOneBoard = new int[10][10];
        opponentTwoBoard = new int[10][10];
        fillBoards();
    }

    //1 - пусто, 2 - корабль, 3 - сюда стреляли
    private void fillBoards(){
        for(int i = 0; i < 10; i++){
            for(int j = 0; j < 10; j++){
                opponentOneBoard[i][j] = 1;
                opponentTwoBoard[i][j] = 1;
            }
        }
    }

    public int[][] getOpponentOneBoard() {
        return opponentOneBoard;
    }

    public int[][] getOpponentTwoBoard() {
        return opponentTwoBoard;
    }
}
