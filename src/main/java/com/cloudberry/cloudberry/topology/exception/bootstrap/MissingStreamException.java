package com.cloudberry.cloudberry.topology.exception.bootstrap;

import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;

public class MissingStreamException extends BootstrappingException {
    private static final HttpStatus STATUS = HttpStatus.NOT_FOUND;

    public MissingStreamException(ObjectId nodeId) {
        super("No outgoing KStream for node: " + nodeId.toHexString() + " was found within the bootstrapping context"
                , STATUS);
    }
}
