package com.cloudberry.cloudberry.service.utility;

import com.cloudberry.cloudberry.db.influx.service.InfluxPropertiesService;
import com.cloudberry.cloudberry.service.api.BucketsService;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BucketNameResolver {
    private final InfluxPropertiesService influxPropertiesService;
    private final BucketsService bucketsService;

    public String getOrDefault(@Nullable String bucketNameOpt) {
        var bucketName = Optional.ofNullable(bucketNameOpt).orElse(influxPropertiesService.getDefaultBucketName());
        return bucketsService.createBucketIfNotExists(bucketName);
    }
}
