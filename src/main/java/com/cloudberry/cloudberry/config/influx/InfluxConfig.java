package com.cloudberry.cloudberry.config.influx;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class InfluxConfig {
    @Value("${influx.buckets.default-logs}")
    private String defaultBucketName;
    @Value("${influx.buckets.default-metrics}")
    private String defaultMetricsBucketName;
    @Value("${influx.buckets.default-logs-from-streams}")
    private String defaultStreamsBucketName;
    @Value("${influx.measurements.default-measurement-name}")
    private String defaultMeasurementName;
    @Value("${influx.organization-name}")
    private String defaultOrganizationName;
}
