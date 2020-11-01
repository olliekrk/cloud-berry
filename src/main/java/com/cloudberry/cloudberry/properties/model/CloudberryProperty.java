package com.cloudberry.cloudberry.properties.model;

import java.util.Arrays;
import java.util.Optional;

public enum CloudberryProperty {

    OVERRIDDEN_DEFAULT_TOPOLOGY_ID("overriddenDefaultTopologyId");

    public final String id;

    CloudberryProperty(String id) {
        this.id = id;
    }

    public static Optional<CloudberryProperty> byId(String id) {
        return Arrays.stream(values()).filter(p -> p.id.equals(id)).findFirst();
    }
}
