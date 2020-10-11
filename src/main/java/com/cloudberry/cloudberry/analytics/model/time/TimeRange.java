package com.cloudberry.cloudberry.analytics.model.time;

import lombok.Value;

import java.time.Instant;

@Value
public class TimeRange {
    Instant start;
    Instant end;
}
