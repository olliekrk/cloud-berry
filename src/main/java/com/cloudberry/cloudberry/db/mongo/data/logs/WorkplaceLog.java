package com.cloudberry.cloudberry.db.mongo.data.logs;

import com.cloudberry.cloudberry.db.common.Parametrized;
import com.cloudberry.cloudberry.kafka.event.EventType;
import com.cloudberry.cloudberry.kafka.event.WorkplaceEvent;
import lombok.EqualsAndHashCode;
import lombok.Value;
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
public class WorkplaceLog extends Log implements Parametrized<String, Object> {
    @Indexed
    long workplaceId;
    Map<String, Object> parameters;

    public WorkplaceLog(Instant time, UUID evaluationId, long workplaceId, Map<String, Object> parameters) {
        super(time, evaluationId);
        this.workplaceId = workplaceId;
        this.parameters = parameters;
    }

    @Override
    public final EventType getType() {
        return EventType.WORKPLACE;
    }

    public static WorkplaceLog ofEvent(WorkplaceEvent event) {
        return new WorkplaceLog(event.getTime(), event.getEvaluationId(), event.getWorkplaceId(), event.getParameters());
    }
}
