package com.cloudberry.cloudberry.analytics.model.filters;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Optional;

@Value
public class DataFiltersWithIds {
    DataFilters filters;
    List<ObjectId> ids;

    @JsonCreator
    public DataFiltersWithIds(
            @JsonProperty("filters") DataFilters filters,
            @JsonProperty("ids") List<ObjectId> ids
    ) {
        this.filters = Optional.ofNullable(filters).orElse(DataFilters.empty());
        this.ids = Optional.ofNullable(ids).orElse(List.of());
    }
}
