package com.cloudberry.cloudberry.rest.exceptions;

public abstract class RestException extends Exception {

    public RestException(String message) {
        super(message);
    }

    public RestException(String message, Throwable cause) {
        super(message, cause);
    }
}
