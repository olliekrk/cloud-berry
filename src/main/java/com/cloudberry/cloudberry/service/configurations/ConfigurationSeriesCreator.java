package com.cloudberry.cloudberry.service.configurations;

import com.cloudberry.cloudberry.analytics.AnalyticsApi;
import com.cloudberry.cloudberry.analytics.model.DataSeries;
import com.cloudberry.cloudberry.analytics.model.InfluxQueryFields;
import com.cloudberry.cloudberry.analytics.model.time.ChronoInterval;
import com.cloudberry.cloudberry.db.mongo.service.MetadataService;
import com.cloudberry.cloudberry.service.api.InfluxUtilService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConfigurationSeriesCreator {
    private final AnalyticsApi analytics;
    private final MetadataService metadataService;
    private final InfluxUtilService influxUtilService;

    public DataSeries createMovingAverageConfigurationSeries(String fieldName,
                                                             InfluxQueryFields influxQueryFields,
                                                             ObjectId configurationId) {
        var seriesName = getConfigurationSeriesName(configurationId);
        var computationsIds = metadataService.findAllComputationIdsForConfiguration(configurationId);
        if (computationsIds.isEmpty()) {
            return DataSeries.empty(seriesName);
        } else {
            var intervalNanos = influxUtilService.averageIntervalNanos(fieldName, computationsIds, influxQueryFields);
            return analytics.getMovingAverageAvg().getTimedMovingSeries(
                    fieldName,
                    ChronoInterval.ofNanos(intervalNanos),
                    computationsIds,
                    influxQueryFields
            ).renamed(seriesName);
        }
    }

    private static String getConfigurationSeriesName(ObjectId configurationId) {
        return String.format("configuration_%s", configurationId.toHexString());
    }

}
