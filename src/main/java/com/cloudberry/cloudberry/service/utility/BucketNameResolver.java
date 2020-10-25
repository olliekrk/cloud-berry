package com.cloudberry.cloudberry.service.utility;

import com.cloudberry.cloudberry.db.influx.service.InfluxPropertiesService;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BucketNameResolver {
    private final InfluxPropertiesService influxPropertiesService;

    public String getOrDefault(@Nullable String bucketNameOpt) {
        return Optional.ofNullable(bucketNameOpt).orElse(influxPropertiesService.getDefaultBucketName());
    }

}
