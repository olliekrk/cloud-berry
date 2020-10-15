package com.cloudberry.cloudberry.rest.exceptions;


import lombok.EqualsAndHashCode;
import lombok.Value;
import org.springframework.http.HttpStatus;

@Value
@EqualsAndHashCode(callSuper = true)
public class FieldNotNumericException extends RestRuntimeException {
    public static final HttpStatus status = HttpStatus.BAD_REQUEST;

    public FieldNotNumericException(String fieldName) {
        super("Field " + fieldName + " is not numeric", status);
    }
}
