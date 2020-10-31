package com.cloudberry.cloudberry.topology.exception.bootstrap;

import org.bson.types.ObjectId;

public class MissingStreamException extends BootstrappingException {
    public MissingStreamException(ObjectId nodeId) {
        super("No outgoing KStream for node: " + nodeId.toHexString() + " was found within the bootstrapping context");
    }
}
