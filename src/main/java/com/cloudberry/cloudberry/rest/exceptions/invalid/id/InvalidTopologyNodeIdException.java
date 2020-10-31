package com.cloudberry.cloudberry.rest.exceptions.invalid.id;

import com.cloudberry.cloudberry.rest.exceptions.RestException;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;

public class InvalidTopologyNodeIdException extends RestException {
    private static final HttpStatus STATUS = HttpStatus.BAD_REQUEST;

    public InvalidTopologyNodeIdException(List<String> ids) {
        super("None of " + Arrays.toString(ids.toArray()) + " is a valid topology node id", STATUS);
    }
}
