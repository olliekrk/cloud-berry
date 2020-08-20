package com.cloudberry.cloudberry.analytics.api;

import com.cloudberry.cloudberry.analytics.model.DataSeries;
import org.bson.types.ObjectId;
import org.springframework.lang.Nullable;

import java.util.List;

public interface SeriesApi {

    List<DataSeries> computationsSeries(String fieldName,
                                        List<ObjectId> computationsIds,
                                        @Nullable String measurementNameOpt,
                                        @Nullable String bucketNameOpt);

    /**
     * Computes the best suitable interval duration, based on average series length and number of series.
     * This is later used as parameter e.g in influx's `timedMovingAverage()`
     */
    Long averageIntervalNanos(String fieldName,
                              List<ObjectId> computationsIds,
                              @Nullable String measurementNameOpt,
                              @Nullable String bucketNameOpt);

}
