package com.cloudberry.cloudberry.rest.exceptions;


import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
public class FieldNotNumericException extends RuntimeException {
    String fieldName;
}
