package com.cloudberry.cloudberry.service;

import com.cloudberry.cloudberry.config.influx.InfluxConfig;
import com.cloudberry.cloudberry.service.api.BucketsService;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BucketNameResolver {
    private final InfluxConfig influxConfig;
    private final BucketsService bucketsService;

    public String getOrDefault(@Nullable String bucketNameOpt) {
        var bucketName = Optional.ofNullable(bucketNameOpt).orElse(influxConfig.getDefaultBucketName());
        return bucketsService.createBucketIfNotExists(bucketName);
    }
}
