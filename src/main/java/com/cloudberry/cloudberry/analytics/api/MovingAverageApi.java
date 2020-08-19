package com.cloudberry.cloudberry.analytics.api;

import com.cloudberry.cloudberry.analytics.model.DataSeries;
import org.bson.types.ObjectId;
import org.springframework.lang.Nullable;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;

public interface MovingAverageApi {

    DataSeries timedMovingAvgSeries(String fieldName,
                                    Long interval,
                                    ChronoUnit chronoUnit,
                                    List<ObjectId> computationsIds,
                                    @Nullable String measurementNameOpt,
                                    @Nullable String bucketNameOpt);

    DataSeries timedMovingStdSeries(String fieldName,
                                    Long interval,
                                    ChronoUnit chronoUnit,
                                    List<ObjectId> computationsIds,
                                    @Nullable String measurementNameOpt,
                                    @Nullable String bucketNameOpt);
}
