package com.cloudberry.cloudberry.topology.exception;

import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;

public class InvalidRootNodeException extends TopologyException {
    private static final HttpStatus STATUS = HttpStatus.INTERNAL_SERVER_ERROR;

    public InvalidRootNodeException(ObjectId nodeId) {
        super("Node with ID " + nodeId.toHexString() + " was not a valid root", STATUS);
    }
}
