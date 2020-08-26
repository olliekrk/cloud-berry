package com.cloudberry.cloudberry.analytics.api;

import com.cloudberry.cloudberry.analytics.model.InfluxQueryFields;
import com.cloudberry.cloudberry.analytics.model.anomalies.AnomalyReport;
import org.bson.types.ObjectId;

import java.util.List;

/**
 * Used for finding parameters' anomalies.
 */
public interface AnomaliesApi {

    List<AnomalyReport> getReportsForComputations(String fieldName,
                                                  List<ObjectId> computationsIds,
                                                  InfluxQueryFields influxQueryFields);

}
