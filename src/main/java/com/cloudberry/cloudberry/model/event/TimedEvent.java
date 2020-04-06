package com.cloudberry.cloudberry.model.event;

import java.time.Instant;

public abstract class TimedEvent {
    public final Instant eventTime;

    protected TimedEvent() {
        this.eventTime = Instant.now();
    }
}
