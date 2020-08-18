package com.cloudberry.cloudberry.rest.exceptions;

import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;

public class InvalidConfigurationIdException extends RestException {
    public static final HttpStatus status = HttpStatus.BAD_REQUEST;

    public InvalidConfigurationIdException(List<String> ids) {
        super("None of " + Arrays.toString(ids.toArray()) + " is a valid configuration id", status);
    }
}