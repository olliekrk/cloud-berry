package com.cloudberry.cloudberry.topology.exception.bootstrap;

import com.cloudberry.cloudberry.topology.exception.TopologyException;
import org.springframework.http.HttpStatus;

public abstract class BootstrappingException extends TopologyException {
    public BootstrappingException(String message) {
        super(message);
    }

    public BootstrappingException(String message, HttpStatus status) {
        super(message, status);
    }
}
