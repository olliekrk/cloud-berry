package com.cloudberry.cloudberry.kafka.event.generic;


import com.cloudberry.cloudberry.kafka.event.Event;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.Map;

@Getter
public class ComputationEvent extends Event {

    private final String measurementName;
    private final Map<String, Object> fields;
    private final Map<String, String> tags;

    @JsonCreator
    public ComputationEvent(@JsonProperty("time") Instant time,
                             @JsonProperty("measurementName") String measurementName,
                             @JsonProperty("fields") Map<String, Object> fields,
                             @JsonProperty("tags") Map<String, String> tags) {
        super(time);
        this.measurementName = measurementName;
        this.fields = fields;
        this.tags = tags;
    }

}
