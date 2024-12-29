package org.example.client.protocol.exception;

public class ExceedingTheMaximumLengthException extends Exception{
    public ExceedingTheMaximumLengthException(String message) {
        super(message);
    }
}
