package com.cloudberry.cloudberry.model.event;

import com.cloudberry.cloudberry.model.Parametrized;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.ToString;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * An initial event, received at the beginning of each evaluation.
 * Contains information about configuration used, problem type and other metadata.
 */
@ToString(callSuper = true)
public class ProblemDefinitionEvent extends Event implements Parametrized<String, Object> {
    public final String name;
    private final Map<String, Object> parameters;

    public ProblemDefinitionEvent(UUID evaluationId,
                                  String name,
                                  Map<String, Object> parameters) {
        super(evaluationId);
        this.name = name;
        this.parameters = parameters;
    }

    @JsonCreator
    private ProblemDefinitionEvent(@JsonProperty("evaluationId") UUID evaluationId,
                                   @JsonProperty("time") Instant time,
                                   @JsonProperty("name") String name,
                                   @JsonProperty("parameters") Map<String, Object> parameters) {
        super(evaluationId, time);
        this.name = name;
        this.parameters = parameters;
    }

    @Override
    public Map<String, Object> getParameters() {
        return parameters;
    }
}
