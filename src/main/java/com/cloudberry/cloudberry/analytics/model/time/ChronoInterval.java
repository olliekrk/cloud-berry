package com.cloudberry.cloudberry.analytics.model.time;

import com.influxdb.query.dsl.functions.properties.TimeInterval;
import lombok.Value;

import java.time.temporal.ChronoUnit;

@Value
public class ChronoInterval {
    long interval;
    ChronoUnit chronoUnit;

    public static ChronoInterval ofNanos(long nanos) {
        return new ChronoInterval(nanos, ChronoUnit.NANOS);
    }

    public TimeInterval asInfluxInterval() {
        return new TimeInterval(interval, chronoUnit);
    }
}
