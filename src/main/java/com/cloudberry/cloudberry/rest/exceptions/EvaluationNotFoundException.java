package com.cloudberry.cloudberry.rest.exceptions;

import lombok.EqualsAndHashCode;
import lombok.Value;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;

@Value
@EqualsAndHashCode(callSuper = true)
public class EvaluationNotFoundException extends RestException {
    public static final HttpStatus status = HttpStatus.NOT_FOUND;

    public EvaluationNotFoundException(ObjectId id) {
        super("Evaluation " + id + " not found", status);
    }
}
