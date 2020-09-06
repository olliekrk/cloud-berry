package com.cloudberry.cloudberry.topology.exception;

import org.bson.types.ObjectId;

public class InvalidRootNodeException extends TopologyException {
    public InvalidRootNodeException(ObjectId nodeId) {
        super("Node with ID " + nodeId.toHexString() + " was not a valid root");
    }
}
