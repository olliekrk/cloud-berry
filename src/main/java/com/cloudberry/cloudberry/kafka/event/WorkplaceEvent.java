package com.cloudberry.cloudberry.kafka.event;

import com.cloudberry.cloudberry.db.common.Parametrized;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Getter
public class WorkplaceEvent extends Event implements Parametrized<String, Object> {
    private final long workplaceId;
    private final Map<String, Object> parameters;

    public WorkplaceEvent(UUID evaluationId, long workplaceId, Map<String, Object> parameters) {
        super(evaluationId);
        this.workplaceId = workplaceId;
        this.parameters = parameters;
    }

    @JsonCreator
    private WorkplaceEvent(@JsonProperty("evaluationId") UUID evaluationId,
                           @JsonProperty("time") Instant time,
                           @JsonProperty("workplaceId") long workplaceId,
                           @JsonProperty("parameters") Map<String, Object> parameters) {
        super(evaluationId, time);
        this.workplaceId = workplaceId;
        this.parameters = parameters;
    }

    @Override
    public Map<String, Object> getParameters() {
        return parameters;
    }

    @Override
    public final EventType getType() {
        return EventType.WORKPLACE;
    }
}
