package com.cloudberry.cloudberry.analytics.api;

import com.cloudberry.cloudberry.analytics.model.DataSeries;
import org.springframework.lang.Nullable;

import java.util.List;

public interface MeanApi {

    // TODO: 19.08.2020 : refactor StatisticsService to get mean data from influx, not to compute everything in memory
    // todo: this signature may/must change
    DataSeries mean(String comparedField,
                    List<DataSeries> series,
                    @Nullable String seriesSuffix);

}
