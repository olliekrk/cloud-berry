package com.cloudberry.cloudberry.model.event;

import com.cloudberry.cloudberry.model.Timed;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

@ToString
abstract class Event implements Timed {
    public final UUID evaluationId;
    public final Instant time;

    protected Event(UUID evaluationId) {
        this.evaluationId = evaluationId;
        this.time = Instant.now();
    }

    protected Event(UUID evaluationId, Instant time) {
        this.evaluationId = evaluationId;
        this.time = time;
    }

    @Override
    public Instant getTime() {
        return time;
    }
}
