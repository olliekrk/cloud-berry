package com.cloudberry.cloudberry.db.influx.service;

import com.cloudberry.cloudberry.config.influx.InfluxConfig;
import com.cloudberry.cloudberry.properties.ApiPropertiesService;
import com.cloudberry.cloudberry.properties.model.InfluxPropertyId;
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
                InfluxPropertyId.OVERRIDDEN_DEFAULT_BUCKET_NAME,
                influxConfig.getDefaultBucketName()
        );
    }

    public void setDefaultBucketName(String defaultBucketName) {
        apiPropertiesService.set(InfluxPropertyId.OVERRIDDEN_DEFAULT_BUCKET_NAME, defaultBucketName);
        bucketsService.createBucketIfNotExists(defaultBucketName);
    }

    public void setProperty(InfluxPropertyId influxProperty, String value) {
        if (influxProperty == InfluxPropertyId.OVERRIDDEN_DEFAULT_BUCKET_NAME) {
            setDefaultBucketName(value);
        }
    }
}
