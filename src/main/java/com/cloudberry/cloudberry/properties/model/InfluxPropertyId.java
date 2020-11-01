package com.cloudberry.cloudberry.properties.model;

import java.util.Arrays;
import java.util.Optional;

public enum InfluxPropertyId implements PropertyId {

    OVERRIDDEN_DEFAULT_BUCKET_NAME("overriddenDefaultBucketName");

    private final String id;

    InfluxPropertyId(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    public static Optional<InfluxPropertyId> byId(String id) {
        return Arrays.stream(values()).filter(p -> p.id.equals(id)).findFirst();
    }
}
