package com.iblog.exception;

public class AppException extends RuntimeException {
    private final int statusCode;

    public AppException(String message) {
        super(message);
        this.statusCode = 400;
    }

    public AppException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
