package com.cloudberry.cloudberry.rest.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public abstract class RestRuntimeException extends RuntimeException {
    @Getter
    private final HttpStatus status;

    public RestRuntimeException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
