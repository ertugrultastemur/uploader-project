package com.example.uploaderservice.exception;

public class DocNotFoundException extends RuntimeException{
    public DocNotFoundException(String message){
        super(message);
    }
}
