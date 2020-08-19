package com.cloudberry.cloudberry.service.api;

import com.cloudberry.cloudberry.analytics.AnalyticsApi;
import com.cloudberry.cloudberry.analytics.model.OptimizationGoal;
import com.cloudberry.cloudberry.analytics.model.OptimizationKind;
import com.cloudberry.cloudberry.db.influx.InfluxDefaults;
import com.cloudberry.cloudberry.db.influx.InfluxDefaults.Columns;
import com.cloudberry.cloudberry.db.influx.service.InfluxDataAccessor;
import com.cloudberry.cloudberry.db.mongo.service.MetadataService;
import com.cloudberry.cloudberry.analytics.model.DataSeries;
import com.cloudberry.cloudberry.common.syntax.ListSyntax;
import com.influxdb.query.FluxRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.Map.of;

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
        var computationSeries = analytics.getSeriesApi().computationsSeries(computationIds, measurementName, bucketName);
        var intervalNanos = getAverageNanoInterval(computationSeries);

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
                    var computationSeries = analytics.getSeriesApi().computationsSeries(computationIds, measurementName, bucketName);
                    var intervalNanos = getAverageNanoInterval(computationSeries);

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
        return analytics
                .getBestSeriesApi()
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
        return analytics
                .getMovingAverageApi()
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
        return analytics
                .getMovingAverageApi()
                .timedMovingStdSeries(
                        fieldName,
                        interval,
                        unit,
                        computationsIds,
                        measurementName,
                        bucketName
                );
    }

    private static long getAverageNanoInterval(List<DataSeries> series) {
        var averageSeriesSize = Math.ceil(getAverageSeriesDuration(series));
        var seriesTimeBounds = getSeriesTotalDuration(series);
        return (long) Math.floor(seriesTimeBounds.toNanos() / Math.max(1, averageSeriesSize));
    }

    private static double getAverageSeriesDuration(List<DataSeries> series) {
        return series
                .stream()
                .map(DataSeries::getData)
                .map(List::size)
                .collect(Collectors.averagingInt(Integer::intValue));
    }

    private static Duration getSeriesTotalDuration(List<DataSeries> series) {
        var stats = series
                .stream()
                .flatMap(s -> s.getData().stream())
                .map(map -> (Instant) map.getOrDefault(Columns.TIME, null))
                .filter(Objects::nonNull)
                .map(Instant::toEpochMilli)
                .collect(Collectors.summarizingLong(Long::longValue));

        return Duration.between(
                Instant.ofEpochMilli(stats.getMin()),
                Instant.ofEpochMilli(stats.getMax())
        );
    }
}
