package com.cloudberry.cloudberry.topology.exception;

import org.bson.types.ObjectId;

public class NodeNotFoundException extends TopologyException {
    public NodeNotFoundException(ObjectId nodeId) {
        super("Node with ID " + nodeId.toHexString() + " was not found");
    }
}
