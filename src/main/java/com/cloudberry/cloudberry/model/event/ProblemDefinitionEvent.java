package com.cloudberry.cloudberry.model.event;

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
public class ProblemDefinitionEvent extends Event {
    public final String name;
    public final Map<String, Object> experimentParameters;
    public final Map<String, Object> configurationParameters;

    public ProblemDefinitionEvent(UUID evaluationId,
                                  String name,
                                  Map<String, Object> experimentParameters,
                                  Map<String, Object> configurationParameters) {
        super(evaluationId);
        this.name = name;
        this.experimentParameters = experimentParameters;
        this.configurationParameters = configurationParameters;
    }

    @JsonCreator
    private ProblemDefinitionEvent(@JsonProperty("evaluationId") UUID evaluationId,
                                   @JsonProperty("time") Instant time,
                                   @JsonProperty("name") String name,
                                   @JsonProperty("experimentParameters") Map<String, Object> experimentParameters,
                                   @JsonProperty("configurationParameters") Map<String, Object> configurationParameters) {
        super(evaluationId, time);
        this.name = name;
        this.experimentParameters = experimentParameters;
        this.configurationParameters = configurationParameters;
    }

}
