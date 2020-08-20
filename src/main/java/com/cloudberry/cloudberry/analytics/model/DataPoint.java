package com.cloudberry.cloudberry.analytics.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

import java.time.Instant;
import java.util.Map;


@Data
@ToString
public class DataPoint {
    private Instant time;
    private Map<String, Object> fields;
    private Map<String, String> tags;

    @JsonCreator
    public DataPoint(@JsonProperty("time") Instant time,
                     @JsonProperty("fields") Map<String, Object> fields,
                     @JsonProperty("tags") Map<String, String> tags) {
        this.time = time;
        this.fields = fields;
        this.tags = tags;
    }
}
