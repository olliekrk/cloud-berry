package com.cloudberry.cloudberry.rest.advice;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class ApiException extends ResponseStatusException {
    public ApiException(HttpStatus status, String reason) {
        super(status, reason);
    }
}
