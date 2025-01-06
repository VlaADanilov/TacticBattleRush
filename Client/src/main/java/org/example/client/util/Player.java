package org.example.client.util;

import lombok.Getter;

@Getter
public enum Player {
    You("You"), Opponent("Op");

    Player(String string) {
        this.string = string;
    }

    private final String string;
}
