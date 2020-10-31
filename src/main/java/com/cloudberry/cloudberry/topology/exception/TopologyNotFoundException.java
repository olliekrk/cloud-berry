package com.cloudberry.cloudberry.topology.exception;

import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;

public class TopologyNotFoundException extends TopologyException {
    private static final HttpStatus STATUS = HttpStatus.NOT_FOUND;

    public TopologyNotFoundException(ObjectId topologyId) {
        super("Topology with ID " + topologyId.toHexString() + " was not found", STATUS);
    }
}
