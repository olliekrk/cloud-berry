package com.cloudberry.cloudberry.topology.exception;

import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;

public class NodeNotFoundException extends TopologyException {
    private static final HttpStatus STATUS = HttpStatus.NOT_FOUND;

    public NodeNotFoundException(ObjectId nodeId) {
        super("Node with ID " + nodeId.toHexString() + " was not found", STATUS);
    }
}
