package com.cloudberry.cloudberry.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MetricsProvider {
    private final MeterRegistry registry;

    public Counter getCounter(String counterName) {
        return registry.counter(counterName);
    }

}
