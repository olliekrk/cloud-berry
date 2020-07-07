package com.cloudberry.cloudberry.model.log;

import com.cloudberry.cloudberry.model.Parametrized;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.bson.types.ObjectId;

import java.time.Instant;
import java.util.Map;

@Value
@EqualsAndHashCode(callSuper = true)
public class WorkplaceLog extends Log implements Parametrized<String, Object> {
    ObjectId evaluationId;
    long workplaceId;
    Map<String, Object> parameters;

    public WorkplaceLog(Instant time, ObjectId evaluationId, long workplaceId, Map<String, Object> parameters) {
        super(time);
        this.evaluationId = evaluationId;
        this.workplaceId = workplaceId;
        this.parameters = parameters;
    }
}
