package com.cloudberry.cloudberry.config;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.statsd.StatsdConfig;
import io.micrometer.statsd.StatsdMeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class MicrometerConfig {

    /**
     * StatsdProperties are taken from application properties, prefixed with: 'management.metrics.export.statsd'
     * and injected by Spring Actuator.
     */
    private final StatsdConfig statsdConfig;

    @Bean
    public MeterRegistry meterRegistry() {
        return StatsdMeterRegistry
                .builder(statsdConfig)
                .clock(Clock.SYSTEM)
                .build();
    }

}
