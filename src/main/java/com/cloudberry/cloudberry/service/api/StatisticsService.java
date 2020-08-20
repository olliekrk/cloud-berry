package com.cloudberry.cloudberry.service.api;

import com.cloudberry.cloudberry.analytics.AnalyticsApi;
import com.cloudberry.cloudberry.analytics.model.DataSeries;
import com.cloudberry.cloudberry.analytics.model.OptimizationGoal;
import com.cloudberry.cloudberry.analytics.model.OptimizationKind;
import com.cloudberry.cloudberry.common.syntax.ListSyntax;
import com.cloudberry.cloudberry.db.mongo.service.MetadataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final AnalyticsApi analytics;
    private final MetadataService metadataService;

    public List<DataSeries> getComputationsByIds(String fieldName,
                                                 @Nullable String measurementName,
                                                 @Nullable String bucketName,
                                                 List<ObjectId> computationIds,
                                                 boolean computeMean) {
        var computationSeries = analytics.getSeriesApi()
                .computationsSeries(fieldName, computationIds, measurementName, bucketName);
        var intervalNanos = analytics.getSeriesApi()
                .averageIntervalNanos(fieldName, computationIds, measurementName, bucketName);

        if (computeMean) {
            var avgSeries = getComputationsAverage(
                    fieldName,
                    intervalNanos,
                    ChronoUnit.NANOS,
                    computationIds,
                    measurementName,
                    bucketName
            );
            return ListSyntax.with(computationSeries, avgSeries);
        } else {
            return computationSeries;
        }
    }

    public List<DataSeries> getComputationsByConfigurationId(String fieldName,
                                                             @Nullable String measurementName,
                                                             @Nullable String bucketName,
                                                             ObjectId configurationId,
                                                             boolean computeMean) {
        var computationIds = metadataService.findAllComputationIdsForConfiguration(configurationId);
        return getComputationsByIds(fieldName, measurementName, bucketName, computationIds, computeMean);
    }

    public List<DataSeries> getConfigurationsMeansByIds(String fieldName,
                                                        @Nullable String measurementName,
                                                        @Nullable String bucketName,
                                                        List<ObjectId> configurationIds) {
        return configurationIds
                .stream()
                .map(configurationId -> {
                    var computationIds = metadataService.findAllComputationIdsForConfiguration(configurationId);
                    var intervalNanos = analytics.getSeriesApi()
                            .averageIntervalNanos(fieldName, computationIds, measurementName, bucketName);

                    return getComputationsAverage(
                            fieldName,
                            intervalNanos,
                            ChronoUnit.NANOS,
                            computationIds,
                            measurementName,
                            bucketName
                    ).renamed(String.format("configuration_%s", configurationId.toHexString()));
                })
                .collect(Collectors.toList());
    }

    public List<DataSeries> getConfigurationsMeansByExperimentName(String fieldName,
                                                                   @Nullable String measurementName,
                                                                   @Nullable String bucketName,
                                                                   String experimentName) {
        var configurationIds = metadataService.findAllConfigurationIdsForExperiment(experimentName);
        return getConfigurationsMeansByIds(fieldName, measurementName, bucketName, configurationIds);
    }

    public List<DataSeries> getNBestComputations(int n,
                                                 String fieldName,
                                                 OptimizationGoal optimizationGoal,
                                                 OptimizationKind optimizationKind,
                                                 String measurementName,
                                                 String bucketName) {
        return analytics.getBestSeriesApi()
                .nBestSeries(
                        n,
                        fieldName,
                        optimizationGoal,
                        optimizationKind,
                        measurementName,
                        bucketName
                );
    }

    public List<DataSeries> getAverageAndStddevOfComputations(String fieldName,
                                                              Long interval,
                                                              ChronoUnit unit,
                                                              List<ObjectId> computationsIds,
                                                              @Nullable String measurementName,
                                                              @Nullable String bucketName) {
        return List.of(
                getComputationsAverage(fieldName, interval, unit, computationsIds, measurementName, bucketName),
                getComputationsStddev(fieldName, interval, unit, computationsIds, measurementName, bucketName)
        );
    }

    private DataSeries getComputationsAverage(String fieldName,
                                              Long interval,
                                              ChronoUnit unit,
                                              List<ObjectId> computationsIds,
                                              @Nullable String measurementName,
                                              @Nullable String bucketName) {
        return analytics.getMovingAverageApi()
                .timedMovingAvgSeries(
                        fieldName,
                        interval,
                        unit,
                        computationsIds,
                        measurementName,
                        bucketName
                );
    }

    private DataSeries getComputationsStddev(String fieldName,
                                             Long interval,
                                             ChronoUnit unit,
                                             List<ObjectId> computationsIds,
                                             @Nullable String measurementName,
                                             @Nullable String bucketName) {
        return analytics.getMovingAverageApi()
                .timedMovingStdSeries(
                        fieldName,
                        interval,
                        unit,
                        computationsIds,
                        measurementName,
                        bucketName
                );
    }

}
