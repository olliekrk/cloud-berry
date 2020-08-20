package com.cloudberry.cloudberry.analytics.api;

import com.cloudberry.cloudberry.analytics.model.DataSeries;
import com.cloudberry.cloudberry.analytics.model.IntervalTime;
import com.cloudberry.cloudberry.analytics.model.OptionalQueryFields;
import org.bson.types.ObjectId;

import java.util.List;

public interface MovingAverageApi {

    DataSeries timedMovingAvgSeries(String fieldName,
                                    IntervalTime intervalTime,
                                    List<ObjectId> computationsIds,
                                    OptionalQueryFields optionalQueryFields);

    DataSeries timedMovingStdSeries(String fieldName,
                                    IntervalTime intervalTime,
                                    List<ObjectId> computationsIds,
                                    OptionalQueryFields optionalQueryFields);
}
