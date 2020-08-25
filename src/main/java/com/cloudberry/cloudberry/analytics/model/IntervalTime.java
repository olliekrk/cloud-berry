package com.cloudberry.cloudberry.analytics.model;

import com.influxdb.query.dsl.functions.properties.TimeInterval;
import lombok.Value;

import java.time.temporal.ChronoUnit;

@Value
public class IntervalTime {
    long interval;
    ChronoUnit chronoUnit;

    public TimeInterval toTimeInterval() {
        return new TimeInterval(interval, chronoUnit);
    }
}
