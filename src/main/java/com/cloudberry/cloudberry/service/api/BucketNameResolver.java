package com.cloudberry.cloudberry.service.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BucketNameResolver {
    @Value("${influx.buckets.default-logs}")
    private String defaultBucketName;

    public String getBucketName(@Nullable String bucketName) {
        return Optional.ofNullable(bucketName).orElse(defaultBucketName);
    }
}
