package com.cloudberry.cloudberry.properties.model;

import java.util.Arrays;
import java.util.Optional;

public enum CloudberryPropertyId implements PropertyId {

    OVERRIDDEN_DEFAULT_TOPOLOGY_ID("overriddenDefaultTopologyId");

    private final String id;

    CloudberryPropertyId(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    public static Optional<CloudberryPropertyId> byId(String id) {
        return Arrays.stream(values()).filter(p -> p.id.equals(id)).findFirst();
    }
}
