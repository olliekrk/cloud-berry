package com.cloudberry.cloudberry.kafka.event.metadata;

import com.cloudberry.cloudberry.kafka.event.Event;
import com.cloudberry.cloudberry.model.Parametrized;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * An initial event, received at the beginning of each evaluation.
 * Contains information about configuration used, problem type and other metadata.
 */
@ToString(callSuper = true)
@Getter
public class MetadataEvent extends Event implements Parametrized<String, Object> {
    private final String name;
    private final UUID evaluationId;
    private final Map<String, Object> experimentParameters;
    private final Map<String, Object> configurationParameters;

    public MetadataEvent(UUID evaluationId,
                         String name,
                         Map<String, Object> experimentParameters,
                         Map<String, Object> configurationParameters) {
        this.evaluationId = evaluationId;
        this.name = name;
        this.experimentParameters = experimentParameters;
        this.configurationParameters = configurationParameters;
    }

    @JsonCreator
    private MetadataEvent(@JsonProperty("evaluationId") UUID evaluationId,
                          @JsonProperty("time") Instant time,
                          @JsonProperty("name") String name,
                          @JsonProperty("experimentParameters") Map<String, Object> experimentParameters,
                          @JsonProperty("configurationParameters") Map<String, Object> configurationParameters) {
        super(time);
        this.evaluationId = evaluationId;
        this.name = name;
        this.experimentParameters = experimentParameters;
        this.configurationParameters = configurationParameters;
    }

    /**
     * Merges both experiment parameters and configuration parameters.
     * In case there are duplicated keys, the experiment parameter value is chosen.
     *
     * @return *new* map with combined parameters
     */
    @Override
    public Map<String, Object> getParameters() {
        return Stream.of(experimentParameters, configurationParameters)
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (experimentValue, __) -> experimentValue
                ));
    }

}
