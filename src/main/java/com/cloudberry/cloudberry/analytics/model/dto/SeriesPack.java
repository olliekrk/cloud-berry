package com.cloudberry.cloudberry.analytics.model.dto;

import com.cloudberry.cloudberry.analytics.model.basic.DataSeries;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;
import lombok.With;

import java.util.List;
import java.util.Optional;

@Value
@With
public class SeriesPack {
    public static final SeriesPack EMPTY = new SeriesPack(List.of(), Optional.empty());
    List<DataSeries> series;
    Optional<DataSeries> averageSeries;

    @JsonCreator
    public SeriesPack(
            @JsonProperty("series") List<DataSeries> series,
            @JsonProperty("averageSeries") Optional<DataSeries> averageSeries
    ) {
        this.series = series;
        this.averageSeries = averageSeries;
    }
}
