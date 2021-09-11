package io.itpl.qss.exception;

public class ImageSizeException extends Exception{
    private String errorMessage;
    public ImageSizeException(String msg){
        super(msg);
        this.errorMessage = msg;
    }

}