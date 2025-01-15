package org.example.server.gameHandler.exception;

import lombok.Getter;

@Getter
public class PlayerException extends Exception{
    private final int player;

    public PlayerException(int i) {
        this.player = i;
    }
}
