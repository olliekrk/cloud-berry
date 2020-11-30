package com.cloudberry.cloudberry.topology.model.bootstrap;

import lombok.Value;
import org.bson.types.ObjectId;

@Value
public class OutgoingEdge {
    ObjectId sourceNodeId;
    String edgeName;
}
