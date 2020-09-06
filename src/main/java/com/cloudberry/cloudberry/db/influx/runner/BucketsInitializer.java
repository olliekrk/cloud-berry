package com.cloudberry.cloudberry.db.influx.runner;

import com.cloudberry.cloudberry.config.influx.InfluxConfig;
import com.cloudberry.cloudberry.service.api.BucketsService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BucketsInitializer implements ApplicationRunner {
    private final InfluxConfig influxConfig;
    private final BucketsService bucketsService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        bucketsService.createBucketIfNotExists(influxConfig.getDefaultBucketName());
        bucketsService.createBucketIfNotExists(influxConfig.getDefaultBucketName());
    }
}
