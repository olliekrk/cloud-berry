package com.cloudberry.cloudberry.db.influx.runner;

import com.cloudberry.cloudberry.config.influx.InfluxConfig;
import com.cloudberry.cloudberry.service.api.BucketsService;
import com.influxdb.exceptions.InfluxException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BucketsInitializer implements ApplicationRunner {
    private final InfluxConfig influxConfig;
    private final BucketsService bucketsService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        try {
            bucketsService.createBucketIfNotExists(influxConfig.getDefaultBucketName());
            bucketsService.createBucketIfNotExists(influxConfig.getDefaultMetricsBucketName());
            bucketsService.createBucketIfNotExists(influxConfig.getDefaultStreamsBucketName());
        } catch (InfluxException e) {
            log.error("Could not initialize influxdb bucket(s). Verify your configuration: {}", e.getMessage());
        }
    }
}
