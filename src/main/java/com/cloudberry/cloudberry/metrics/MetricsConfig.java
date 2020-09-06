package com.cloudberry.cloudberry.metrics;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.influx.InfluxMeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class MetricsConfig {
    private final MetricsInfluxConfig metricsInfluxConfig;
    private final Clock micrometerClock;

    @Bean
    public MeterRegistry meterRegistry() {
        return new InfluxMeterRegistry(metricsInfluxConfig, micrometerClock);
    }

}
