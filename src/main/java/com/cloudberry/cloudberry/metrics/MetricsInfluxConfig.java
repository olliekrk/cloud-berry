package com.cloudberry.cloudberry.metrics;

import io.micrometer.influx.InfluxConfig;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Setter
@ConfigurationProperties(prefix = "management.metrics.export.influx")
@Configuration
public class MetricsInfluxConfig implements InfluxConfig {
    private String db;
    private String uri;
    private String userName;
    private String password;
    private Duration step;
    private int batchSize;
    private boolean autoCreateDb;

    @Override
    public String db() {
        return db;
    }

    @Override
    public String userName() {
        return userName;
    }

    @Override
    public String password() {
        return password;
    }

    @Override
    public String uri() {
        return uri;
    }

    @Override
    public boolean autoCreateDb() {
        return autoCreateDb;
    }

    @Override
    public int batchSize() {
        return batchSize;
    }

    @Override
    public Duration step() {
        return step;
    }

    @Override
    public String get(String _key) {
        return null; // null to use defaults
    }
}
