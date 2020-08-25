package com.cloudberry.cloudberry.service;

import com.cloudberry.cloudberry.config.influx.InfluxConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BucketNameResolver {
    private final InfluxConfig influxConfig;

    public String getOrDefault(@Nullable String bucketName) {
        return Optional.ofNullable(bucketName).orElse(influxConfig.getDefaultBucketName());
    }
}
