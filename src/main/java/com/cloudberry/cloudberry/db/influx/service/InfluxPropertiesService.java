package com.cloudberry.cloudberry.db.influx.service;

import com.cloudberry.cloudberry.config.influx.InfluxConfig;
import com.cloudberry.cloudberry.properties.ApiPropertiesService;
import com.cloudberry.cloudberry.properties.model.ApiPropertyKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class InfluxPropertiesService {
    private final InfluxConfig influxConfig;
    private final ApiPropertiesService apiPropertiesService;

    public String getDefaultBucketName() {
        return apiPropertiesService.getOrDefault(
                ApiPropertyKey.OVERRIDDEN_DEFAULT_BUCKET_NAME,
                influxConfig.getDefaultBucketName()
        );
    }

    public void setDefaultBucketName(String defaultBucketName) {
        apiPropertiesService.set(ApiPropertyKey.OVERRIDDEN_DEFAULT_BUCKET_NAME, defaultBucketName);
    }

}
