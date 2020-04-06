package com.cloudberry.cloudberry.model.event;

import java.util.Map;

public class WorkplaceEvent extends TimedEvent {
    public final Long workplaceId;
    public final Map<Object, Object> workplaceParameters;

    public WorkplaceEvent(Long workplaceId, Map<Object, Object> workplaceParameters) {
        this.workplaceId = workplaceId;
        this.workplaceParameters = workplaceParameters;
    }
}
