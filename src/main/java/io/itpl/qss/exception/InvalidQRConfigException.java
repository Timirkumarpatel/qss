package io.itpl.qss.exception;

public class InvalidQRConfigException extends Exception{
    private String errorMessage;
    public InvalidQRConfigException(String msg){
        super(msg);
        this.errorMessage = msg;

    }
}