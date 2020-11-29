package com.cloudberry.cloudberry.analytics.model.filters;

import com.cloudberry.cloudberry.analytics.model.thresholds.Thresholds;
import com.cloudberry.cloudberry.rest.exceptions.InvalidThresholdsException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Optional;

@Value
public class DataFiltersWithThresholdsAndIds {
    DataFilters filters;
    Thresholds thresholds;
    List<ObjectId> ids;

    @JsonCreator
    public DataFiltersWithThresholdsAndIds(
            @JsonProperty("filters") DataFilters filters,
            @JsonProperty("thresholds") Thresholds thresholds,
            @JsonProperty("ids") List<ObjectId> ids
    ) throws InvalidThresholdsException {
        this.filters = Optional.ofNullable(filters).orElse(DataFilters.empty());
        this.thresholds = Optional.ofNullable(thresholds).orElse(Thresholds.empty()).requireValid();
        this.ids = Optional.ofNullable(ids).orElse(List.of());
    }
}
