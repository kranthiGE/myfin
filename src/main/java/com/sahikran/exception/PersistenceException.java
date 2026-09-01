package com.sahikran.exception;

public class PersistenceException extends RuntimeException {
    
    public PersistenceException(String message){
        super(message);
    }

    public PersistenceException(String message, Throwable throwable){
        super(message, throwable);
    }
}
