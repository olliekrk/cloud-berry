package com.cloudberry.cloudberry.model.event;

import com.cloudberry.cloudberry.model.Timed;

import java.time.Instant;
import java.util.UUID;

public abstract class Event implements Timed {
    public final UUID evaluationId;
    public final Instant time;

    protected Event(UUID evaluationId) {
        this.evaluationId = evaluationId;
        this.time = Instant.now();
    }

    @Override
    public Instant getTime() {
        return time;
    }
}
