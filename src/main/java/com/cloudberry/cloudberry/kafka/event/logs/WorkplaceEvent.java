package com.cloudberry.cloudberry.kafka.event.logs;

import com.cloudberry.cloudberry.kafka.event.Event;
import com.cloudberry.cloudberry.model.Parametrized;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Getter
public class WorkplaceEvent extends Event implements Parametrized<String, Object> {
    private final UUID evaluationId;
    private final long workplaceId;
    private final Map<String, Object> parameters;

    public WorkplaceEvent(UUID evaluationId, long workplaceId, Map<String, Object> parameters) {
        this.evaluationId = evaluationId;
        this.workplaceId = workplaceId;
        this.parameters = parameters;
    }

    @JsonCreator
    private WorkplaceEvent(@JsonProperty("evaluationId") UUID evaluationId,
                           @JsonProperty("time") Instant time,
                           @JsonProperty("workplaceId") long workplaceId,
                           @JsonProperty("parameters") Map<String, Object> parameters) {
        super(time);
        this.evaluationId = evaluationId;
        this.workplaceId = workplaceId;
        this.parameters = parameters;
    }

    @Override
    public Map<String, Object> getParameters() {
        return parameters;
    }

}