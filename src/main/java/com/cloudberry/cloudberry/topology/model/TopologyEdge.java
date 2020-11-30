package com.cloudberry.cloudberry.topology.model;

import lombok.Data;
import org.bson.types.ObjectId;

import java.io.Serializable;

@Data
public class TopologyEdge implements Serializable {
    public static final String DEFAULT_NAME = "DEFAULT";

    private final String name;
    private final ObjectId source;
    private final ObjectId target;

    public static TopologyEdge defaultEdge(ObjectId source, ObjectId target) {
        return new TopologyEdge(DEFAULT_NAME, source, target);
    }

}
