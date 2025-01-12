package org.example.utils;

import lombok.Getter;
import lombok.Setter;

import java.net.Socket;
import java.util.Objects;

@Getter
public class OnePlayerInRoom {
    private Socket socket;
    @Setter
    private boolean ready;
    @Setter
    private int countOfUnits;

    public OnePlayerInRoom(Socket socket) {
        this.socket = socket;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        OnePlayerInRoom that = (OnePlayerInRoom) object;
        return Objects.equals(socket, that.socket);
    }

}
