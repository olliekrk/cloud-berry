package com.cloudberry.cloudberry.rest.exceptions;

import lombok.EqualsAndHashCode;
import lombok.Value;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;

@Value
@EqualsAndHashCode(callSuper = true)
public class ComputationNotFoundException extends RestException {
    public static final HttpStatus status = HttpStatus.NOT_FOUND;

    public ComputationNotFoundException(ObjectId id) {
        super("Computation " + id + " not found", status);
    }
}
