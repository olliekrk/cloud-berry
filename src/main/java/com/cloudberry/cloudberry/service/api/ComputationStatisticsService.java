package com.cloudberry.cloudberry.service.api;

import com.cloudberry.cloudberry.analytics.AnalyticsApi;
import com.cloudberry.cloudberry.analytics.model.CriteriaMode;
import com.cloudberry.cloudberry.analytics.model.basic.DataSeries;
import com.cloudberry.cloudberry.analytics.model.dto.SeriesPack;
import com.cloudberry.cloudberry.analytics.model.optimization.Optimization;
import com.cloudberry.cloudberry.analytics.model.query.InfluxQueryFields;
import com.cloudberry.cloudberry.analytics.model.thresholds.Thresholds;
import com.cloudberry.cloudberry.analytics.model.thresholds.ThresholdsInfo;
import com.cloudberry.cloudberry.analytics.model.thresholds.ThresholdsType;
import com.cloudberry.cloudberry.analytics.model.time.ChronoInterval;
import com.cloudberry.cloudberry.analytics.service.average.moving.MovingAverageInMemoryOps;
import com.cloudberry.cloudberry.db.mongo.service.MetadataService;
import com.cloudberry.cloudberry.service.configurations.ConfigurationSeriesCreator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ComputationStatisticsService {
    private final AnalyticsApi analytics;
    private final MetadataService metadataService;
    private final ConfigurationSeriesCreator configurationSeriesCreator;

    public SeriesPack getComputationsByIds(
            String fieldName,
            InfluxQueryFields influxQueryFields,
            List<ObjectId> computationIds
    ) {
        var computationsSeries = analytics.getSeriesApi()
                .computationsSeries(fieldName, computationIds, influxQueryFields);

        var computationsAverage = Optional
                .of(MovingAverageInMemoryOps.movingAverageSeries(computationsSeries, fieldName));

        return new SeriesPack(computationsSeries, computationsAverage);
    }

    public SeriesPack getComputationsByConfigurationId(
            String fieldName,
            InfluxQueryFields influxQueryFields,
            ObjectId configurationId
    ) {
        var computationIds = metadataService.findAllComputationIdsForConfiguration(configurationId);
        return getComputationsByIds(fieldName, influxQueryFields, computationIds);
    }

    public SeriesPack getNBestComputations(
            int n,
            String fieldName,
            Optimization optimization,
            InfluxQueryFields influxQueryFields
    ) {
        var bestComputations = analytics.getBestSeriesApi()
                .nBestSeries(n, fieldName, optimization, influxQueryFields);

        var bestComputationsAverage = Optional
                .of(MovingAverageInMemoryOps.movingAverageSeries(bestComputations, fieldName));

        return new SeriesPack(bestComputations, bestComputationsAverage);
    }

    public SeriesPack getNBestComputationsForConfiguration(
            int n,
            String fieldName,
            Optimization optimization,
            InfluxQueryFields influxQueryFields,
            ObjectId configurationId
    ) {
        var computationsIds = metadataService.findAllComputationIdsForConfiguration(configurationId);
        var bestComputations = analytics.getBestSeriesApi()
                .nBestSeriesFrom(n, fieldName, optimization, influxQueryFields, computationsIds);

        var bestComputationsAverage = Optional
                .of(MovingAverageInMemoryOps.movingAverageSeries(bestComputations, fieldName));

        return new SeriesPack(bestComputations, bestComputationsAverage);
    }

    public SeriesPack getAverageAndStddevOfComputations(
            String fieldName,
            ChronoInterval chronoInterval,
            List<ObjectId> computationsIds,
            InfluxQueryFields influxQueryFields
    ) {
        return List.of(
                getComputationsAverage(fieldName, chronoInterval, computationsIds, influxQueryFields),
                getComputationsStddev(fieldName, chronoInterval, computationsIds, influxQueryFields)
        );
    }

    public SeriesPack getComputationsExceedingThresholds(
            String fieldName,
            Thresholds thresholds,
            CriteriaMode mode,
            InfluxQueryFields influxQueryFields
    ) {
        return analytics.getThresholdsApi().thresholdsExceedingSeries(fieldName, thresholds, mode, influxQueryFields);
    }

    public List<DataSeries> getComputationsExceedingThresholdsForConfiguration(
            String fieldName,
            Thresholds thresholds,
            CriteriaMode mode,
            InfluxQueryFields influxQueryFields,
            ObjectId configurationId
    ) {
        return analytics.getThresholdsApi()
                .thresholdsExceedingSeriesFrom(
                        fieldName,
                        thresholds,
                        mode,
                        influxQueryFields,
                        metadataService.findAllComputationIdsForConfiguration(configurationId)
                );
    }

    public List<DataSeries> getComputationsExceedingThresholdsRelatively(
            String fieldName,
            Thresholds thresholds,
            ThresholdsType thresholdsType,
            CriteriaMode mode,
            InfluxQueryFields influxQueryFields,
            ObjectId configurationId
    ) {
        var allConfigurationSeries =
                getComputationsByConfigurationId(fieldName, influxQueryFields, configurationId, false);
        var averageConfigurationSeries =
                configurationSeriesCreator
                        .createMovingAverageConfigurationSeries(fieldName, influxQueryFields, configurationId);
        return analytics.getThresholdsApi().thresholdsExceedingSeriesRelatively(
                fieldName,
                new ThresholdsInfo(thresholds, thresholdsType),
                mode,
                allConfigurationSeries,
                averageConfigurationSeries
        );
    }

    private DataSeries getComputationsAverage(
            String fieldName,
            ChronoInterval chronoInterval,
            List<ObjectId> computationsIds,
            InfluxQueryFields influxQueryFields
    ) {
        return analytics.getMovingAverageAvg()
                .getTimedMovingSeries(
                        fieldName,
                        chronoInterval,
                        computationsIds,
                        influxQueryFields
                );
    }

    private DataSeries getComputationsStddev(
            String fieldName,
            ChronoInterval chronoInterval,
            List<ObjectId> computationsIds,
            InfluxQueryFields influxQueryFields
    ) {
        return analytics.getMovingAverageStd()
                .getTimedMovingSeries(
                        fieldName,
                        chronoInterval,
                        computationsIds,
                        influxQueryFields
                );
    }
}
