package com.cloudberry.cloudberry.topology.exception;

import org.bson.types.ObjectId;

public class EdgeWouldInduceCycleException extends TopologyException {
    public EdgeWouldInduceCycleException(ObjectId sourceId, ObjectId targetId) {
        super("Edge from node: %s to node: %s would create a cycle".formatted(sourceId, targetId));
    }
}
