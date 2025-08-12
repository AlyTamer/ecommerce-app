package com.aly.ecomapp.exception;

public class CategoryException extends RuntimeException {
    public CategoryException(String message) {
        super(message);
    }
    public CategoryException(String message, Throwable e){
        super(message,e);
    }
}
