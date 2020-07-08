package com.cloudberry.cloudberry.kafka.event;

import com.cloudberry.cloudberry.model.Timed;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;

@ToString
@AllArgsConstructor
@Getter
public abstract class Event implements Timed {
    private final Instant time;

    protected Event() {
        this.time = Instant.now();
    }

}
