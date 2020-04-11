package com.cloudberry.cloudberry.model.event;

import com.cloudberry.cloudberry.model.Parametrized;

import java.util.Map;
import java.util.UUID;

public class WorkplaceEvent extends Event implements Parametrized<String, Object> {
    public final long workplaceId;
    private final Map<String, Object> parameters;

    public WorkplaceEvent(UUID evaluationId, long workplaceId, Map<String, Object> parameters) {
        super(evaluationId);
        this.workplaceId = workplaceId;
        this.parameters = parameters;
    }

    @Override
    public Map<String, Object> getParameters() {
        return parameters;
    }
}
