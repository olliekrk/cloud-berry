package com.cloudberry.cloudberry.model.event;

import com.cloudberry.cloudberry.model.Timed;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

@ToString
@AllArgsConstructor
abstract class Event implements Timed {
    public final UUID evaluationId;
    @Getter
    public final Instant time;

    protected Event(UUID evaluationId) {
        this.evaluationId = evaluationId;
        this.time = Instant.now();
    }
}
