package com.cloudberry.cloudberry.model.event;

import com.cloudberry.cloudberry.model.Parametrized;

import java.util.Map;
import java.util.UUID;

/**
 * An initial event, received at the beginning of each evaluation.
 * Contains information about configuration used, problem type and other metadata.
 */
public class ProblemDefinitionEvent extends Event implements Parametrized<String, Object> {
    public final String name;
    private final Map<String, Object> parameters;

    public ProblemDefinitionEvent(UUID evaluationId, String name, Map<String, Object> parameters) {
        super(evaluationId);
        this.name = name;
        this.parameters = parameters;
    }

    public ProblemDefinitionEvent(UUID evaluationId, String name) {
        this(evaluationId, name, Map.of());
    }

    @Override
    public Map<String, Object> getParameters() {
        return parameters;
    }
}
