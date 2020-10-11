package com.cloudberry.cloudberry.service.api;

import com.cloudberry.cloudberry.analytics.AnalyticsApi;
import com.cloudberry.cloudberry.analytics.model.*;
import com.cloudberry.cloudberry.analytics.model.optimization.Optimization;
import com.cloudberry.cloudberry.common.syntax.ListSyntax;
import com.cloudberry.cloudberry.db.mongo.service.MetadataService;
import com.cloudberry.cloudberry.service.series.ConfigurationSeriesCreator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final AnalyticsApi analytics;
    private final MetadataService metadataService;
    private final InfluxUtilService influxUtilService;
    private final ConfigurationSeriesCreator configurationSeriesCreator;

    public List<DataSeries> getComputationsByIds(String fieldName,
                                                 InfluxQueryFields influxQueryFields,
                                                 List<ObjectId> computationIds,
                                                 boolean computeMean) {
        var computationSeries = analytics.getSeriesApi()
                .computationsSeries(fieldName, computationIds, influxQueryFields);

        if (computeMean) {
            var intervalNanos = influxUtilService
                    .averageIntervalNanos(fieldName, computationIds, influxQueryFields);

            var avgSeries = getComputationsAverage(
                    fieldName,
                    ChronoInterval.ofNanos(intervalNanos),
                    computationIds,
                    influxQueryFields
            );
            return ListSyntax.with(computationSeries, avgSeries);
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

    public List<DataSeries> getConfigurationsMeansByIds(String fieldName,
                                                        InfluxQueryFields influxQueryFields,
                                                        List<ObjectId> configurationIds) {
        return ListSyntax.mapped(
                configurationIds,
                configurationId -> configurationSeriesCreator.createMovingAverageConfigurationSeries(
                        fieldName,
                        influxQueryFields,
                        configurationId
                )
        );
    }

    public List<DataSeries> getConfigurationsMeansByExperimentName(String fieldName,
                                                                   InfluxQueryFields influxQueryFields,
                                                                   String experimentName) {
        var configurationIds = metadataService.findAllConfigurationIdsForExperiment(experimentName);
        return getConfigurationsMeansByIds(fieldName, influxQueryFields, configurationIds);
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
