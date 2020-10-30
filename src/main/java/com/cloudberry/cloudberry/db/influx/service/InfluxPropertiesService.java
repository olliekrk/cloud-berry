package com.cloudberry.cloudberry.db.influx.service;

import com.cloudberry.cloudberry.config.influx.InfluxConfig;
import com.cloudberry.cloudberry.properties.ApiPropertiesService;
import com.cloudberry.cloudberry.properties.model.InfluxProperty;
import com.cloudberry.cloudberry.service.api.BucketsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class InfluxPropertiesService {
    private final InfluxConfig influxConfig;
    private final BucketsService bucketsService;
    private final ApiPropertiesService apiPropertiesService;

    public String getDefaultBucketName() {
        return apiPropertiesService.getOrDefault(
                InfluxProperty.OVERRIDDEN_DEFAULT_BUCKET_NAME.id,
                influxConfig.getDefaultBucketName()
        );
    }

    public void setDefaultBucketName(String defaultBucketName) {
        apiPropertiesService.set(InfluxProperty.OVERRIDDEN_DEFAULT_BUCKET_NAME.id, defaultBucketName);
        bucketsService.createBucketIfNotExists(defaultBucketName);
    }

    public void setProperty(InfluxProperty influxProperty, String value) {
        if (influxProperty == InfluxProperty.OVERRIDDEN_DEFAULT_BUCKET_NAME) {
            setDefaultBucketName(value);
        }
    }
}
