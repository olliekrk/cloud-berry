package com.cloudberry.cloudberry.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MetricsRegistry {
    private final MeterRegistry meterRegistry;

    public void incrementCounter(String counterName) {
        meterRegistry.counter(counterName).increment();
    }

}
