package com.cloudberry.cloudberry.rest.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import java.util.Map;
import java.util.Optional;

@Value
public class DataFilters {
    Map<String, Object> fieldFilters;
    Map<String, String> tagFilters;

    @JsonCreator
    public DataFilters(@JsonProperty("fieldFilters") Map<String, Object> fieldFilters,
                       @JsonProperty("tagFilters") Map<String, String> tagFilters) {
        this.fieldFilters = Optional.ofNullable(fieldFilters).orElse(Map.of());
        this.tagFilters = Optional.ofNullable(tagFilters).orElse(Map.of());
    }
}
