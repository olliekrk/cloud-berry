package com.cloudberry.cloudberry.rest.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public abstract class RestException extends Exception {
    @Getter
    private final HttpStatus status;

    public RestException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public RestException(String message, Throwable cause, HttpStatus status) {
        super(message, cause);
        this.status = status;
    }
}
