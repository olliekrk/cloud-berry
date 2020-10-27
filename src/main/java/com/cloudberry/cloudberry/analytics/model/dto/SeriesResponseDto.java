package com.cloudberry.cloudberry.analytics.model.dto;

import com.cloudberry.cloudberry.analytics.model.basic.DataSeries;
import lombok.Value;

import java.util.List;
import java.util.Optional;

@Value
public class SeriesResponseDto {
    List<DataSeries> series;
    Optional<DataSeries> averageSeries;
}
