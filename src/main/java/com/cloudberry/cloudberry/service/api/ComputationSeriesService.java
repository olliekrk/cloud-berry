package com.cloudberry.cloudberry.service.api;

import com.cloudberry.cloudberry.analytics.AnalyticsApi;
import com.cloudberry.cloudberry.analytics.model.CriteriaMode;
import com.cloudberry.cloudberry.analytics.model.dto.SeriesPack;
import com.cloudberry.cloudberry.analytics.model.filters.DataFilters;
import com.cloudberry.cloudberry.analytics.model.optimization.Optimization;
import com.cloudberry.cloudberry.analytics.model.query.InfluxQueryFields;
import com.cloudberry.cloudberry.analytics.model.thresholds.Thresholds;
import com.cloudberry.cloudberry.analytics.model.thresholds.ThresholdsInfo;
import com.cloudberry.cloudberry.analytics.model.thresholds.ThresholdsType;
import com.cloudberry.cloudberry.analytics.service.average.moving.MovingAverageInMemoryOps;
import com.cloudberry.cloudberry.db.mongo.service.MetadataService;
import com.cloudberry.cloudberry.service.configurations.ConfigurationSeriesNameResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ComputationSeriesService {
    private final AnalyticsApi analytics;
    private final MetadataService metadataService;
    private final ConfigurationSeriesNameResolver configurationSeriesNameResolver;

    public SeriesPack getComputations(
            String fieldName,
            InfluxQueryFields influxQueryFields,
            List<ObjectId> computationIds,
            DataFilters dataFilters
    ) {
        var series =
                analytics.getSeriesApi().computationsSeries(fieldName, computationIds, influxQueryFields, dataFilters);
        var average = MovingAverageInMemoryOps.movingAverageSeries(series, fieldName);

        return new SeriesPack(series, average);
    }

    public SeriesPack getComputations(
            String fieldName,
            InfluxQueryFields influxQueryFields,
            ObjectId configurationId,
            DataFilters dataFilters
    ) {
        var computationIds = metadataService.findAllComputationIdsForConfiguration(configurationId);
        var pack = getComputations(fieldName, influxQueryFields, computationIds, dataFilters);
        var packAverageRenamed = pack
                .getAverageSeries()
                .map(s -> s.withSeriesInfo(configurationSeriesNameResolver.configurationSeriesInfo(configurationId)));

        return pack.withAverageSeries(packAverageRenamed);
    }

    public SeriesPack getNBestComputations(
            int n,
            String fieldName,
            Optimization optimization,
            InfluxQueryFields influxQueryFields,
            DataFilters dataFilters
    ) {
        var bestN =
                analytics.getBestSeriesApi().nBestSeries(n, fieldName, optimization, influxQueryFields, dataFilters);
        var bestNAverage = MovingAverageInMemoryOps.movingAverageSeries(bestN, fieldName);

        return new SeriesPack(bestN, bestNAverage);
    }

    public SeriesPack getNBestComputationsForConfiguration(
            int n,
            String fieldName,
            Optimization optimization,
            InfluxQueryFields influxQueryFields,
            ObjectId configurationId,
            DataFilters dataFilters
    ) {
        var computationsIds = metadataService.findAllComputationIdsForConfiguration(configurationId);
        var bestN = analytics.getBestSeriesApi()
                .nBestSeriesFrom(n, fieldName, optimization, influxQueryFields, computationsIds, dataFilters);
        var bestNAverage = MovingAverageInMemoryOps.movingAverageSeries(bestN, fieldName)
                .map(s -> s.withSeriesInfo(configurationSeriesNameResolver.configurationSeriesInfo(configurationId)));

        return new SeriesPack(bestN, bestNAverage);
    }

    public SeriesPack getComputationsExceedingThresholds(
            String fieldName,
            Thresholds thresholds,
            CriteriaMode mode,
            InfluxQueryFields influxQueryFields,
            DataFilters dataFilters
    ) {
        var exceeding = analytics.getThresholdsApi()
                .thresholdsExceedingSeries(fieldName, thresholds, mode, influxQueryFields, dataFilters);
        var exceedingAverage = MovingAverageInMemoryOps.movingAverageSeries(exceeding, fieldName);

        return new SeriesPack(exceeding, exceedingAverage);
    }

    public SeriesPack getComputationsExceedingThresholdsForConfiguration(
            String fieldName,
            Thresholds thresholds,
            CriteriaMode mode,
            InfluxQueryFields influxQueryFields,
            ObjectId configurationId,
            DataFilters dataFilters
    ) {
        var computationIds = metadataService.findAllComputationIdsForConfiguration(configurationId);
        if (computationIds.isEmpty()) {
            return SeriesPack.EMPTY;
        }
        var exceeding = analytics.getThresholdsApi()
                .thresholdsExceedingSeriesFrom(
                        fieldName,
                        thresholds,
                        mode,
                        influxQueryFields,
                        computationIds,
                        dataFilters
                );
        var exceedingAverage = MovingAverageInMemoryOps.movingAverageSeries(exceeding, fieldName)
                .map(s -> s.withSeriesInfo(configurationSeriesNameResolver.configurationSeriesInfo(configurationId)));
        return new SeriesPack(exceeding, exceedingAverage);
    }

    public SeriesPack getComputationsExceedingThresholdsRelatively(
            String fieldName,
            Thresholds thresholds,
            ThresholdsType thresholdsType,
            CriteriaMode mode,
            InfluxQueryFields influxQueryFields,
            ObjectId configurationId,
            DataFilters dataFilters
    ) {
        var series = getComputations(fieldName, influxQueryFields, configurationId, dataFilters);
        if (series.getSeries().isEmpty() || series.getAverageSeries().isEmpty()) {
            return SeriesPack.EMPTY;
        }

        var thresholdsInfo = new ThresholdsInfo(thresholds, thresholdsType);
        var exceeding = analytics.getThresholdsApi().thresholdsExceedingSeriesRelatively(
                fieldName,
                thresholdsInfo,
                mode,
                series.getSeries(),
                series.getAverageSeries().get()
        );
        var exceedingAverage = MovingAverageInMemoryOps.movingAverageSeries(exceeding, fieldName)
                .map(s -> s.withSeriesInfo(configurationSeriesNameResolver.configurationSeriesInfo(configurationId)));

        return new SeriesPack(exceeding, exceedingAverage);
    }

}
