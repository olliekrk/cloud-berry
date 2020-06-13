package com.cloudberry.cloudberry.rest.exceptions;

import lombok.EqualsAndHashCode;
import lombok.Value;

import java.util.UUID;

@Value
@EqualsAndHashCode(callSuper = true)
public class EvaluationNotFoundException extends Exception {
    UUID evaluationId;
}
