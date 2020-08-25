package com.cloudberry.cloudberry.analytics.model;

import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;

import java.util.Optional;

@RequiredArgsConstructor
public class InfluxQueryFields {
    @Nullable
    private final String measurementName;
    private final String bucketName;

    public Optional<String> getMeasurementNameOptional() {
        return Optional.ofNullable(measurementName);
    }

    public String getBucketName() {
        return bucketName;
    }
}
