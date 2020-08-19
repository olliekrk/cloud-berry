package com.cloudberry.cloudberry.service.api;

import com.cloudberry.cloudberry.analytics.AnalyticsApi;
import com.cloudberry.cloudberry.analytics.model.OptimizationGoal;
import com.cloudberry.cloudberry.analytics.model.OptimizationKind;
import com.cloudberry.cloudberry.db.influx.InfluxDefaults;
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

import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Map.of;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final AnalyticsApi analytics;
    private final MetadataService metadataService;
    private final InfluxDataAccessor influxDataAccessor;

    public List<DataSeries> getComputationsByIds(String comparedField,
                                                 @Nullable String measurementName,
                                                 @Nullable String bucketName,
                                                 List<ObjectId> computationIds,
                                                 boolean computeMean) {
        var computationSeries = computationIds
                .stream()
                .map(id -> getComputationSeries(measurementName, bucketName, id))
                .filter(DataSeries::nonEmpty)
                .collect(Collectors.toList());

        if (computeMean) {
            return ListSyntax.with(computationSeries, analytics.getMeanApi().mean(comparedField, computationSeries, null));
        } else {
            return computationSeries;
        }
    }

    public List<DataSeries> getComputationsByConfigurationId(String comparedField,
                                                             @Nullable String measurementName,
                                                             @Nullable String bucketName,
                                                             ObjectId configurationId,
                                                             boolean computeMean) {
        var computationIds = metadataService.findAllComputationIdsForConfiguration(configurationId);
        return getComputationsByIds(comparedField, measurementName, bucketName, computationIds, computeMean);
    }

    public List<DataSeries> getConfigurationsMeansByIds(String comparedField,
                                                        @Nullable String measurementName,
                                                        @Nullable String bucketName,
                                                        List<ObjectId> configurationIds) {
        return configurationIds
                .stream()
                .map(configurationId -> {
                    var series = getComputationsByConfigurationId(
                            comparedField,
                            measurementName,
                            bucketName,
                            configurationId,
                            false
                    );
                    return analytics.getMeanApi().mean(comparedField, series, configurationId.toHexString());
                })
                .collect(Collectors.toList());
    }

    public List<DataSeries> getConfigurationsMeansByExperimentName(String comparedField,
                                                                   @Nullable String measurementName,
                                                                   @Nullable String bucketName,
                                                                   String experimentName) {
        var configurationIds = metadataService.findAllConfigurationIdsForExperiment(experimentName);
        return getConfigurationsMeansByIds(comparedField, measurementName, bucketName, configurationIds);
    }

    private DataSeries getComputationSeries(@Nullable String measurementName,
                                            @Nullable String bucketName,
                                            ObjectId computationId) {
        var computationIdHex = computationId.toHexString();
        var records = influxDataAccessor.findData(
                bucketName,
                measurementName,
                Collections.emptyMap(),
                of(InfluxDefaults.CommonTags.COMPUTATION_ID, computationIdHex));

        return new DataSeries(computationIdHex, ListSyntax.mapped(records, FluxRecord::getValues));
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

    public List<DataSeries> computationsAverageAndStddev(String fieldName,
                                                         Long interval,
                                                         ChronoUnit unit,
                                                         List<ObjectId> computationsIds,
                                                         @Nullable String measurementName,
                                                         @Nullable String bucketName) {
        return List.of(
                computationsAverage(fieldName, interval, unit, computationsIds, measurementName, bucketName),
                computationsStddev(fieldName, interval, unit, computationsIds, measurementName, bucketName)
        );
    }

    private DataSeries computationsAverage(String fieldName,
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

    private DataSeries computationsStddev(String fieldName,
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
}
