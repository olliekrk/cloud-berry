package com.cloudberry.cloudberry.topology.exception;

import org.bson.types.ObjectId;

public class MissingRootNodeException extends TopologyException {
    public MissingRootNodeException(ObjectId topologyId) {
        super("Topology with ID " + topologyId.toHexString() + " has missing root node(s)");
    }
}
