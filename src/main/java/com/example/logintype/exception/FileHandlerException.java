package com.example.logintype.exception;

public class FileHandlerException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 6859523072077046176L;

    public FileHandlerException() {
        super();
    }

    public FileHandlerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public FileHandlerException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileHandlerException(String message) {
        super(message);
    }

    public FileHandlerException(Throwable cause) {
        super(cause);
    }

}
