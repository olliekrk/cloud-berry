package com.cloudberry.cloudberry.rest.advice;

import com.cloudberry.cloudberry.rest.exceptions.RestException;
import com.cloudberry.cloudberry.rest.exceptions.RestRuntimeException;
import com.cloudberry.cloudberry.topology.exception.TopologyException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Handlers for uncaught exceptions thrown within REST controllers.
 */
@Slf4j
@RestControllerAdvice
public class ApiErrorsAdvice {

    @ExceptionHandler(RestException.class)
    public ApiError handleRest(RestException exception) {
        return logErrors(exception, exception.getStatus());
    }

    @ExceptionHandler(RestRuntimeException.class)
    public ApiError handleRestRuntime(RestException exception) {
        return logErrors(exception, exception.getStatus());
    }

    @ExceptionHandler(TopologyException.class)
    public ApiError handleRestRuntime(TopologyException exception) {
        return logErrors(exception, exception.getStatus());
    }

    private ApiError logErrors(Throwable t, HttpStatus status) {
        log.error(String.format("Cannot process REST API request - %s - %s", status.toString(), t.getMessage()));
        return new ApiError(t.getMessage(), status.value());
    }
}
