package com.cloudberry.cloudberry.rest.exceptions;


import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
public class FieldNotNumericException extends RuntimeException {
    String fieldName;
}
