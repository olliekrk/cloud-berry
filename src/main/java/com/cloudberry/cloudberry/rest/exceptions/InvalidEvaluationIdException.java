package com.cloudberry.cloudberry.rest.exceptions;

import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;

public class InvalidEvaluationIdException extends RestException {
    public static final HttpStatus status = HttpStatus.BAD_REQUEST;

    public InvalidEvaluationIdException(List<String> ids) {
        super("None of " + Arrays.toString(ids.toArray()) + " is a valid evaluation id", status);
    }
}
