package com.cloudberry.cloudberry.kafka.event.generic;


import com.cloudberry.cloudberry.kafka.event.Event;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.With;

import java.time.Instant;
import java.util.Map;

@ToString
@Getter
@AllArgsConstructor
public class ComputationEvent extends Event {

    private final String measurementName;
    @With
    private final Map<String, Object> fields;
    @With
    private final Map<String, String> tags;

    @JsonCreator
    public ComputationEvent(
            @JsonProperty("time") Instant time,
            @JsonProperty("measurementName") String measurementName,
            @JsonProperty("fields") Map<String, Object> fields,
            @JsonProperty("tags") Map<String, String> tags
    ) {
        super(time);
        this.measurementName = measurementName;
        this.fields = fields;
        this.tags = tags;
    }

}
