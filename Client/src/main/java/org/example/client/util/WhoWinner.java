package org.example.client.util;




public class WhoWinner {
    private static int WIN;

    public static int getWinner() {
        return WIN;
    }

    public static void setWinner(int winner) {
        if(winner != 0 && winner != 1){
            throw new RuntimeException();
        }
        WIN = winner;
    }
}
