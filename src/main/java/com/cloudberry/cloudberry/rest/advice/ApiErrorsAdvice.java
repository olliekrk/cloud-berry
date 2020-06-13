package com.cloudberry.cloudberry.rest.advice;

import com.cloudberry.cloudberry.rest.exceptions.EvaluationNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Handlers for uncaught exceptions thrown within REST controllers.
 */
@RestControllerAdvice
public class ApiErrorsAdvice {

    // todo: not in use yet, but cool.

    @ExceptionHandler(EvaluationNotFoundException.class)
    public ApiError handleExperimentNotFoundException(EvaluationNotFoundException exception) {
        return new ApiError("No experiment found for ID: " + exception.getEvaluationId());
    }
}
