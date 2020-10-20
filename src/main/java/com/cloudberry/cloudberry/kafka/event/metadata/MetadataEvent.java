package com.cloudberry.cloudberry.kafka.event.metadata;

import com.cloudberry.cloudberry.common.trait.Parametrized;
import com.cloudberry.cloudberry.kafka.event.Event;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;
import org.bson.types.ObjectId;

import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * An initial event, received at the beginning of each computation.
 * Contains information about configuration used, problem type and other metadata.
 */
@ToString(callSuper = true)
@Getter
public class MetadataEvent extends Event implements Parametrized<String, Object> {
    private final String experimentName;
    private final ObjectId computationId;
    private final Map<String, Object> experimentParameters;
    private final Map<String, Object> configurationParameters;

    public MetadataEvent(
            ObjectId computationId,
            String experimentName,
            Map<String, Object> experimentParameters,
            Map<String, Object> configurationParameters
    ) {
        this.computationId = computationId;
        this.experimentName = experimentName;
        this.experimentParameters = experimentParameters;
        this.configurationParameters = configurationParameters;
    }

    @JsonCreator
    private MetadataEvent(
            @JsonProperty("computationId") ObjectId computationId,
            @JsonProperty("time") Instant time,
            @JsonProperty("experimentName") String experimentName,
            @JsonProperty("experimentParameters") Map<String, Object> experimentParameters,
            @JsonProperty("configurationParameters") Map<String, Object> configurationParameters
    ) {
        super(time);
        this.computationId = computationId;
        this.experimentName = experimentName;
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
