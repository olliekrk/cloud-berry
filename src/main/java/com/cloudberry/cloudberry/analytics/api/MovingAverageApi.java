package com.cloudberry.cloudberry.analytics.api;

import com.cloudberry.cloudberry.analytics.model.ChronoInterval;
import com.cloudberry.cloudberry.analytics.model.DataSeries;
import com.cloudberry.cloudberry.analytics.model.InfluxQueryFields;
import org.bson.types.ObjectId;

import java.util.List;

public interface MovingAverageApi {

    DataSeries getTimedMovingSeries(String fieldName,
                                    ChronoInterval chronoInterval,
                                    List<ObjectId> computationsIds,
                                    InfluxQueryFields influxQueryFields);
}
