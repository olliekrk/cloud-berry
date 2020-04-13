package com.cloudberry.cloudberry.model.logs;

import com.cloudberry.cloudberry.model.Timed;
import com.cloudberry.cloudberry.model.event.EventType;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.time.Instant;
import java.util.UUID;

@Data
public abstract class Log implements Timed {
    @Id
    protected ObjectId id;
    protected final Instant time;
    @Indexed
    @Field(targetType = FieldType.STRING)
    protected final UUID evaluationId;

    protected Log(Instant time, UUID evaluationId) {
        this.time = time;
        this.evaluationId = evaluationId;
    }

    public abstract EventType getType();
}
