package com.cloudberry.cloudberry.service.api;

import com.cloudberry.cloudberry.analytics.AnalyticsApi;
import com.cloudberry.cloudberry.analytics.model.DataSeries;
import com.cloudberry.cloudberry.analytics.model.IntervalTime;
import com.cloudberry.cloudberry.analytics.model.OptionalQueryFields;
import com.cloudberry.cloudberry.analytics.model.optimization.Optimization;
import com.cloudberry.cloudberry.common.syntax.ListSyntax;
import com.cloudberry.cloudberry.db.mongo.service.MetadataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticsService {

    private static final ChronoUnit INTERVAL_UNIT = ChronoUnit.NANOS;
    private final AnalyticsApi analytics;
    private final MetadataService metadataService;
    private final InfluxUtilService influxUtilService;

    public List<DataSeries> getComputationsByIds(String fieldName,
                                                 OptionalQueryFields optionalQueryFields,
                                                 List<ObjectId> computationIds,
                                                 boolean computeMean) {
        var computationSeries = analytics.getSeriesApi()
                .computationsSeries(fieldName, computationIds, optionalQueryFields);

        if (computeMean) {
            var intervalNanos = influxUtilService
                    .averageIntervalNanos(fieldName, computationIds, optionalQueryFields);

            var avgSeries = getComputationsAverage(
                    fieldName,
                    new IntervalTime(intervalNanos, INTERVAL_UNIT),
                    computationIds,
                    optionalQueryFields
            );
            return ListSyntax.with(computationSeries, avgSeries);
        } else {
            return computationSeries;
        }
    }

    public List<DataSeries> getComputationsByConfigurationId(String fieldName,
                                                             OptionalQueryFields optionalQueryFields,
                                                             ObjectId configurationId,
                                                             boolean computeMean) {
        var computationIds = metadataService.findAllComputationIdsForConfiguration(configurationId);
        return getComputationsByIds(fieldName, optionalQueryFields, computationIds, computeMean);
    }

    public List<DataSeries> getConfigurationsMeansByIds(String fieldName,
                                                        OptionalQueryFields optionalQueryFields,
                                                        List<ObjectId> configurationIds) {
        return ListSyntax.mapped(configurationIds, configurationId -> {
            var computationIds = metadataService.findAllComputationIdsForConfiguration(configurationId);
            var intervalNanos = influxUtilService
                    .averageIntervalNanos(fieldName, computationIds, optionalQueryFields);

            return getComputationsAverage(
                    fieldName,
                    new IntervalTime(intervalNanos, INTERVAL_UNIT),
                    computationIds,
                    optionalQueryFields
            ).renamed(String.format("configuration_%s", configurationId.toHexString()));
        });
    }

    public List<DataSeries> getConfigurationsMeansByExperimentName(String fieldName,
                                                                   OptionalQueryFields optionalQueryFields,
                                                                   String experimentName) {
        var configurationIds = metadataService.findAllConfigurationIdsForExperiment(experimentName);
        return getConfigurationsMeansByIds(fieldName, optionalQueryFields, configurationIds);
    }

    public List<DataSeries> getNBestComputations(int n,
                                                 String fieldName,
                                                 Optimization optimization,
                                                 OptionalQueryFields optionalQueryFields) {
        return analytics.getBestSeriesApi()
                .nBestSeries(
                        n,
                        fieldName,
                        optimization,
                        optionalQueryFields
                );
    }

    public List<DataSeries> getAverageAndStddevOfComputations(String fieldName,
                                                              IntervalTime intervalTime,
                                                              List<ObjectId> computationsIds,
                                                              OptionalQueryFields optionalQueryFields) {
        return List.of(
                getComputationsAverage(fieldName, intervalTime, computationsIds, optionalQueryFields),
                getComputationsStddev(fieldName, intervalTime, computationsIds, optionalQueryFields)
        );
    }

    private DataSeries getComputationsAverage(String fieldName,
                                              IntervalTime intervalTime,
                                              List<ObjectId> computationsIds,
                                              OptionalQueryFields optionalQueryFields) {
        return analytics.getMovingAverageAvg()
                .getTimedMovingSeries(
                        fieldName,
                        intervalTime,
                        computationsIds,
                        optionalQueryFields
                );
    }

    private DataSeries getComputationsStddev(String fieldName,
                                             IntervalTime intervalTime,
                                             List<ObjectId> computationsIds,
                                             OptionalQueryFields optionalQueryFields) {
        return analytics.getMovingAverageStd()
                .getTimedMovingSeries(
                        fieldName,
                        intervalTime,
                        computationsIds,
                        optionalQueryFields
                );
    }

}
