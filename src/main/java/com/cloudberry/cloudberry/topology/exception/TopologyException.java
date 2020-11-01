package com.cloudberry.cloudberry.topology.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public abstract class TopologyException extends RuntimeException {
    @Getter
    private final HttpStatus status;

    public TopologyException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public TopologyException(String message) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST;
    }
}
