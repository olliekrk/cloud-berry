package com.cloudberry.cloudberry.rest.advice;

import com.cloudberry.cloudberry.rest.exceptions.RestException;
import com.cloudberry.cloudberry.rest.exceptions.RestRuntimeException;
import com.cloudberry.cloudberry.topology.exception.TopologyException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Handlers for uncaught exceptions thrown within REST controllers.
 */
@Slf4j
@RestControllerAdvice
public class ApiErrorsAdvice {

    @ExceptionHandler(RestException.class)
    public void handleRest(RestException exception) {
        logException(new ApiException(exception.getStatus(), exception.getMessage()));
    }

    @ExceptionHandler(RestRuntimeException.class)
    public void handleRestRuntime(RestRuntimeException exception) {
        logException(new ApiException(exception.getStatus(), exception.getMessage()));
    }

    @ExceptionHandler(TopologyException.class)
    public void handleTopologyException(TopologyException exception) {
        logException(new ApiException(exception.getStatus(), exception.getMessage()));
    }

    private void logException(ApiException e) {
        log.error(String.format("Cannot process REST API request - %s - %s", e.getStatus().toString(), e.getMessage()));
        throw e;
    }

}
