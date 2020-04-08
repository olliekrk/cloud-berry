package com.cloudberry.cloudberry.model.logs;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(value = "logs")
public abstract class Log {
    @Id
    String id;
    public final Instant eventTime;

    protected Log(Instant eventTime) {
        this.eventTime = eventTime;
    }
}
