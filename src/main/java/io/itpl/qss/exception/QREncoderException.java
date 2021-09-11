package io.itpl.qss.exception;

public class QREncoderException extends Exception{
    private String errorMessage;
    public QREncoderException(String msg){
        super(msg);
        this.errorMessage = msg;

    }
}