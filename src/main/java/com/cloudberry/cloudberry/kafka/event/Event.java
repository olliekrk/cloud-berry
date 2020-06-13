package com.cloudberry.cloudberry.kafka.event;

import com.cloudberry.cloudberry.db.common.Timed;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

@ToString
@AllArgsConstructor
@Getter
public abstract class Event implements Timed {
    private final UUID evaluationId;
    private final Instant time;

    protected Event(UUID evaluationId) {
        this.evaluationId = evaluationId;
        this.time = Instant.now();
    }

    public abstract EventType getType();
}