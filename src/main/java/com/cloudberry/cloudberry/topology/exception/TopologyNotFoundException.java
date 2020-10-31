package com.cloudberry.cloudberry.topology.exception;

import org.bson.types.ObjectId;

public class TopologyNotFoundException extends TopologyException {
    public TopologyNotFoundException(ObjectId topologyId) {
        super("Topology with ID " + topologyId.toHexString() + " was not found");
    }
}
