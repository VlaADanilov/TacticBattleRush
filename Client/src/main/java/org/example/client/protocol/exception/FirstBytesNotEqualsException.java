package org.example.client.protocol.exception;

public class FirstBytesNotEqualsException extends Exception{
    public FirstBytesNotEqualsException(String message){
        super(message);
    }
}
