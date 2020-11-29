package com.cloudberry.cloudberry.analytics.model.filters;

import com.cloudberry.cloudberry.analytics.model.thresholds.Thresholds;
import com.cloudberry.cloudberry.rest.exceptions.InvalidThresholdsException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import java.util.Optional;

@Value
public class DataFiltersWithThresholds {
    DataFilters filters;
    Thresholds thresholds;

    @JsonCreator
    public DataFiltersWithThresholds(
            @JsonProperty("filters") DataFilters filters,
            @JsonProperty("thresholds") Thresholds thresholds
    ) throws InvalidThresholdsException {
        this.filters = Optional.ofNullable(filters).orElse(DataFilters.empty());
        this.thresholds = Optional.ofNullable(thresholds).orElse(Thresholds.empty()).requireValid();
    }
}
