package com.cloudberry.cloudberry.model.event;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ProblemDefinitionEvent extends TimedEvent {
    public final String description;

    public ProblemDefinitionEvent(@JsonProperty("description") String description) {
        this.description = description;
    }
}
