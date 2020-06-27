package com.cloudberry.cloudberry.model.log;

import com.cloudberry.cloudberry.model.Timed;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
public abstract class Log implements Timed {
    protected final Instant time;
}
