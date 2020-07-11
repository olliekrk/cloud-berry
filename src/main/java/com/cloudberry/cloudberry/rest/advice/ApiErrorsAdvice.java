package com.cloudberry.cloudberry.rest.advice;

import com.cloudberry.cloudberry.rest.exceptions.ConfigurationIdInvalidException;
import com.cloudberry.cloudberry.rest.exceptions.EvaluationIdInvalidException;
import com.cloudberry.cloudberry.rest.exceptions.EvaluationNotFoundException;
import com.cloudberry.cloudberry.rest.exceptions.FieldNotNumericException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Handlers for uncaught exceptions thrown within REST controllers.
 */
@RestControllerAdvice
public class ApiErrorsAdvice {

    @ExceptionHandler(EvaluationNotFoundException.class)
    public ApiError handleExperimentNotFound(EvaluationNotFoundException exception) {
        return new ApiError("No experiment found for ID: " + exception.getEvaluationId());
    }

    @ExceptionHandler(EvaluationIdInvalidException.class)
    public ApiError handleEvaluationIdInvalid(EvaluationIdInvalidException exception) {
        return new ApiError("You must provide at least one valid evaluation ID");
    }

    @ExceptionHandler(ConfigurationIdInvalidException.class)
    public ApiError handleConfigurationIdInvalid(ConfigurationIdInvalidException exception) {
        return new ApiError("You must provide at least one valid configuration ID");
    }

    @ExceptionHandler(FieldNotNumericException.class)
    public ApiError handleFieldNotNumeric(FieldNotNumericException exception) {
        return new ApiError(exception.getFieldName() + "is not a numeric field");
    }
}
