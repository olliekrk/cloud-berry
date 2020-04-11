package com.cloudberry.cloudberry.model.logs;

import com.cloudberry.cloudberry.model.Timed;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.Instant;
import java.util.UUID;

@Data
public abstract class Log implements Timed {
    @Id
    protected ObjectId id;
    protected Instant time;
    @Indexed
    protected UUID evaluationId;

    @Override
    public Instant getTime() {
        return time;
    }
}
