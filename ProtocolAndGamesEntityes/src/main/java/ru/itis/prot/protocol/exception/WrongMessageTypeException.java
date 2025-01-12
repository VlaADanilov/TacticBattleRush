package ru.itis.prot.protocol.exception;

public class WrongMessageTypeException extends Exception{
    public WrongMessageTypeException(String message) {
        super(message);
    }
}
