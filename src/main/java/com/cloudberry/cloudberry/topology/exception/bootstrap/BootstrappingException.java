package com.cloudberry.cloudberry.topology.exception.bootstrap;

import com.cloudberry.cloudberry.topology.exception.TopologyException;

public abstract class BootstrappingException extends TopologyException {
    public BootstrappingException(String message) {
        super(message);
    }
}
