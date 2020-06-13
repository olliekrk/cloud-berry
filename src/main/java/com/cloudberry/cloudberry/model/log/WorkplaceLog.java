package com.cloudberry.cloudberry.model.log;

import com.cloudberry.cloudberry.model.Parametrized;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Value
@EqualsAndHashCode(callSuper = true)
public class WorkplaceLog extends Log implements Parametrized<String, Object> {
    long workplaceId;
    Map<String, Object> parameters;

    public WorkplaceLog(Instant time, UUID evaluationId, long workplaceId, Map<String, Object> parameters) {
        super(time, evaluationId);
        this.workplaceId = workplaceId;
        this.parameters = parameters;
    }
}
