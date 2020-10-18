package com.cloudberry.cloudberry.service.api;

import com.cloudberry.cloudberry.analytics.AnalyticsApi;
import com.cloudberry.cloudberry.analytics.model.CriteriaMode;
import com.cloudberry.cloudberry.analytics.model.DataSeries;
import com.cloudberry.cloudberry.analytics.model.InfluxQueryFields;
import com.cloudberry.cloudberry.analytics.model.Thresholds;
import com.cloudberry.cloudberry.analytics.model.ChronoInterval;
import com.cloudberry.cloudberry.analytics.model.CriteriaMode;
import com.cloudberry.cloudberry.analytics.model.DataSeries;
import com.cloudberry.cloudberry.analytics.model.InfluxQueryFields;
import com.cloudberry.cloudberry.analytics.model.Thresholds;
import com.cloudberry.cloudberry.analytics.model.optimization.Optimization;
import com.cloudberry.cloudberry.analytics.model.time.ChronoInterval;
import com.cloudberry.cloudberry.analytics.service.average.moving.MovingAverage;
import com.cloudberry.cloudberry.common.syntax.ListSyntax;
import com.cloudberry.cloudberry.db.mongo.service.MetadataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Supplier;

@Slf4j
@Service
@RequiredArgsConstructor
public class ComputationStatisticsService {
    private final AnalyticsApi analytics;
    private final MetadataService metadataService;
    private final InfluxUtilService influxUtilService;

    public List<DataSeries> getComputationsByIds(String fieldName,
                                                 InfluxQueryFields influxQueryFields,
                                                 List<ObjectId> computationIds,
                                                 boolean computeMean) {
        var computationSeries = analytics.getSeriesApi()
                .computationsSeries(fieldName, computationIds, influxQueryFields);

        if (computeMean) {
            Supplier<DataSeries> getAverageSeries = () -> {
                if (computationSeries.stream().anyMatch(DataSeries::nonEmpty)) {
                    var intervalNanos = influxUtilService.averageIntervalNanos(fieldName, computationIds, influxQueryFields);
                    return getComputationsAverage(
                            fieldName,
                            ChronoInterval.ofNanos(intervalNanos),
                            computationIds,
                            influxQueryFields
                    );
                } else {
                    return DataSeries.empty(MovingAverage.AVG_SERIES_NAME);
                }
            };
            return ListSyntax.with(computationSeries, getAverageSeries.get());
        } else {
            return computationSeries;
        }
    }

    public List<DataSeries> getComputationsByConfigurationId(String fieldName,
                                                             InfluxQueryFields influxQueryFields,
                                                             ObjectId configurationId,
                                                             boolean computeMean) {
        var computationIds = metadataService.findAllComputationIdsForConfiguration(configurationId);
        if (computationIds.isEmpty()) {
            return List.of();
        }
        return getComputationsByIds(fieldName, influxQueryFields, computationIds, computeMean);
    }

    public List<DataSeries> getNBestComputations(int n,
                                                 String fieldName,
                                                 Optimization optimization,
                                                 InfluxQueryFields influxQueryFields) {
        return analytics.getBestSeriesApi()
                .nBestSeries(
                        n,
                        fieldName,
                        optimization,
                        influxQueryFields
                );
    }

    public List<DataSeries> getNBestComputationsForConfiguration(int n,
                                                                 String fieldName,
                                                                 Optimization optimization,
                                                                 InfluxQueryFields influxQueryFields,
                                                                 ObjectId configurationId) {
        return analytics.getBestSeriesApi()
                .nBestSeriesFrom(
                        n,
                        fieldName,
                        optimization,
                        influxQueryFields,
                        metadataService.findAllComputationIdsForConfiguration(configurationId)
                );
    }

    public List<DataSeries> getAverageAndStddevOfComputations(String fieldName,
                                                              ChronoInterval chronoInterval,
                                                              List<ObjectId> computationsIds,
                                                              InfluxQueryFields influxQueryFields) {
        return List.of(
                getComputationsAverage(fieldName, chronoInterval, computationsIds, influxQueryFields),
                getComputationsStddev(fieldName, chronoInterval, computationsIds, influxQueryFields)
        );
    }

    public List<DataSeries> getComputationsExceedingThresholds(String fieldName,
                                                               Thresholds thresholds,
                                                               CriteriaMode mode,
                                                               InfluxQueryFields influxQueryFields) {
        return analytics.getThresholdsApi().thresholdsExceedingSeries(fieldName, thresholds, mode, influxQueryFields);
    }

    public List<DataSeries> getComputationsExceedingThresholdsForConfiguration(String fieldName,
                                                                               Thresholds thresholds,
                                                                               CriteriaMode mode,
                                                                               InfluxQueryFields influxQueryFields,
                                                                               ObjectId configurationId) {
        return analytics.getThresholdsApi()
                .thresholdsExceedingSeriesFrom(
                        fieldName,
                        thresholds,
                        mode,
                        influxQueryFields,
                        metadataService.findAllComputationIdsForConfiguration(configurationId)
                );
    }

    private DataSeries getComputationsAverage(String fieldName,
                                              ChronoInterval chronoInterval,
                                              List<ObjectId> computationsIds,
                                              InfluxQueryFields influxQueryFields) {
        return analytics.getMovingAverageAvg()
                .getTimedMovingSeries(
                        fieldName,
                        chronoInterval,
                        computationsIds,
                        influxQueryFields
                );
    }

    private DataSeries getComputationsStddev(String fieldName,
                                             ChronoInterval chronoInterval,
                                             List<ObjectId> computationsIds,
                                             InfluxQueryFields influxQueryFields) {
        return analytics.getMovingAverageStd()
                .getTimedMovingSeries(
                        fieldName,
                        chronoInterval,
                        computationsIds,
                        influxQueryFields
                );
    }

}
