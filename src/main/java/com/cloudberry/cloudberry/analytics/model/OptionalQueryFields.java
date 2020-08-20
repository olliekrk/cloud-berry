package com.cloudberry.cloudberry.analytics.model;

import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class OptionalQueryFields {
    private final String measurementName;
    private final String bucketName;

    public Optional<String> getMeasurementNameOptional() {
        return Optional.ofNullable(measurementName);
    }

    public Optional<String> getBucketNameOptional() {
        return Optional.ofNullable(bucketName);
    }
}
