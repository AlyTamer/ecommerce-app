package com.aly.ecomapp.exception;

public class OrderHistoryException extends RuntimeException {
    public OrderHistoryException(String message) {
        super(message);
    }
    public OrderHistoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
