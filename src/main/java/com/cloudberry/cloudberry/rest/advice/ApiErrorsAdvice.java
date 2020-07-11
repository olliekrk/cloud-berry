package com.cloudberry.cloudberry.rest.advice;

import com.cloudberry.cloudberry.rest.exceptions.ConfigurationIdInvalidException;
import com.cloudberry.cloudberry.rest.exceptions.EvaluationIdInvalidException;
import com.cloudberry.cloudberry.rest.exceptions.EvaluationNotFoundException;
import com.cloudberry.cloudberry.rest.exceptions.FieldNotNumericException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Handlers for uncaught exceptions thrown within REST controllers.
 */
@Slf4j
@RestControllerAdvice
public class ApiErrorsAdvice {

    @ExceptionHandler(EvaluationNotFoundException.class)
    public ApiError handleExperimentNotFound(EvaluationNotFoundException exception) {
        return logErrors(exception, "No experiment found for ID: " + exception.getEvaluationId());
    }

    @ExceptionHandler(EvaluationIdInvalidException.class)
    public ApiError handleEvaluationIdInvalid(EvaluationIdInvalidException exception) {
        return logErrors(exception, "You must provide at least one valid evaluation ID");
    }

    @ExceptionHandler(ConfigurationIdInvalidException.class)
    public ApiError handleConfigurationIdInvalid(ConfigurationIdInvalidException exception) {
        return logErrors(exception, "You must provide at least one valid configuration ID");
    }

    @ExceptionHandler(FieldNotNumericException.class)
    public ApiError handleFieldNotNumeric(FieldNotNumericException exception) {
        return logErrors(exception, exception.getFieldName() + " is not a numeric field");
    }

    private ApiError logErrors(Throwable t, String message) {
        log.warn(message, t.getMessage());
        return new ApiError(message);
    }
}
