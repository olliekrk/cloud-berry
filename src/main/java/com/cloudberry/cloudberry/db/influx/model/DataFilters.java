package com.cloudberry.cloudberry.db.influx.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Value
public class DataFilters {
    Map<String, Object> fieldFilters;
    Map<String, String> tagFilters;
    Set<String> tagPresence;

    @JsonCreator
    public DataFilters(
            @JsonProperty("fieldFilters") Map<String, Object> fieldFilters,
            @JsonProperty("tagFilters") Map<String, String> tagFilters,
            @JsonProperty("tagPresence") Set<String> tagPresence
    ) {
        this.fieldFilters = Optional.ofNullable(fieldFilters).orElse(Map.of());
        this.tagFilters = Optional.ofNullable(tagFilters).orElse(Map.of());
        this.tagPresence = Optional.ofNullable(tagPresence).orElse(Set.of());
    }
}
