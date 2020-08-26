package com.cloudberry.cloudberry.service.api;

import com.cloudberry.cloudberry.analytics.api.AnomaliesApi;
import com.cloudberry.cloudberry.analytics.model.InfluxQueryFields;
import com.cloudberry.cloudberry.analytics.model.anomalies.AnomalyReport;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnomaliesService {
    private final AnomaliesApi anomaliesApi;

    public List<AnomalyReport> getReports(String fieldName,
                                           List<ObjectId> computationsIds,
                                           InfluxQueryFields influxQueryFields) {
        return anomaliesApi.getReportsForComputations(fieldName, computationsIds, influxQueryFields);
    }
}
