package com.cloudberry.cloudberry.properties.model;

import java.util.Arrays;
import java.util.Optional;

public enum InfluxProperty {

    OVERRIDDEN_DEFAULT_BUCKET_NAME("overriddenDefaultBucketName");

    public final String id;

    InfluxProperty(String id) {
        this.id = id;
    }

    public static Optional<InfluxProperty> byId(String id) {
        return Arrays.stream(values()).filter(p -> p.id.equals(id)).findFirst();
    }
}
