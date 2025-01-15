package ru.itis.prot.protocol.exception;

public class ExceedingTheMaximumLengthException extends Exception{
    public ExceedingTheMaximumLengthException(String message) {
        super(message);
    }
}
