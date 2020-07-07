package com.cloudberry.cloudberry.db.mongo.data.logs;

import com.cloudberry.cloudberry.model.Parametrized;
import com.cloudberry.cloudberry.kafka.event.logs.WorkplaceEvent;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * W log from AgE
 */
@EqualsAndHashCode(callSuper = true)
@Value
@Document(collection = "workplace_log")
public class MongoWorkplaceLog extends MongoLog implements Parametrized<String, Object> {
    @Indexed
    long workplaceId;
    Map<String, Object> parameters;

    public MongoWorkplaceLog(Instant time, ObjectId evaluationId, long workplaceId, Map<String, Object> parameters) {
        super(time, evaluationId);
        this.workplaceId = workplaceId;
        this.parameters = parameters;
    }

    @Override
    public final MongoLogType getType() {
        return MongoLogType.WORKPLACE;
    }

    public static MongoWorkplaceLog ofEvent(WorkplaceEvent event) {
        return new MongoWorkplaceLog(event.getTime(), event.getEvaluationId(), event.getWorkplaceId(), event.getParameters());
    }
}
