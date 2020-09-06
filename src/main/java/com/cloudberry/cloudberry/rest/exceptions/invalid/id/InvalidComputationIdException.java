package com.cloudberry.cloudberry.rest.exceptions.invalid.id;

import com.cloudberry.cloudberry.rest.exceptions.RestException;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;

public class InvalidComputationIdException extends RestException {
    private static final HttpStatus STATUS = HttpStatus.BAD_REQUEST;

    public InvalidComputationIdException(List<String> ids) {
        super("None of " + Arrays.toString(ids.toArray()) + " is a valid computation id", STATUS);
    }
}