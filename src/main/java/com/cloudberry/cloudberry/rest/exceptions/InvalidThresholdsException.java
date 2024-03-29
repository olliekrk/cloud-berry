package com.cloudberry.cloudberry.rest.exceptions;

import com.cloudberry.cloudberry.analytics.model.thresholds.Thresholds;
import org.springframework.http.HttpStatus;

public class InvalidThresholdsException extends RestException {
    private static final HttpStatus status = HttpStatus.BAD_REQUEST;

    public InvalidThresholdsException(Thresholds thresholds) {
        super(
                "At least one of thresholds must be specified and lower bound must not be greater than upper bound",
                status
        );
    }

}
