package com.cloudberry.cloudberry.topology.exception;

import org.bson.types.ObjectId;

public class NodeNotInTopologyException extends TopologyException {
    public NodeNotInTopologyException(ObjectId nodeId, ObjectId topologyId) {
        super(String.format("Node with ID %s doesn't belong to topology with ID %s", nodeId.toHexString(),
                            topologyId.toHexString()
        ));
    }
}
