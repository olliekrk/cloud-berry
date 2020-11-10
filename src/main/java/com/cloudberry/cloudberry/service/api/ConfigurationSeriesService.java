package com.cloudberry.cloudberry.service.api;

import com.cloudberry.cloudberry.analytics.AnalyticsApi;
import com.cloudberry.cloudberry.analytics.model.CriteriaMode;
import com.cloudberry.cloudberry.analytics.model.basic.DataSeries;
import com.cloudberry.cloudberry.analytics.model.dto.SeriesPack;
import com.cloudberry.cloudberry.analytics.model.optimization.Optimization;
import com.cloudberry.cloudberry.analytics.model.query.InfluxQueryFields;
import com.cloudberry.cloudberry.analytics.model.thresholds.Thresholds;
import com.cloudberry.cloudberry.analytics.service.average.moving.MovingAverageInMemoryOps;
import com.cloudberry.cloudberry.db.mongo.service.MetadataService;
import com.cloudberry.cloudberry.service.configurations.ConfigurationSeriesCreator;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConfigurationSeriesService {
    private final AnalyticsApi analyticsApi;
    private final MetadataService metadataService;
    private final ConfigurationSeriesCreator configurationSeriesCreator;

    public SeriesPack getNBestConfigurations(
            int n,
            String fieldName,
            Optimization optimization,
            InfluxQueryFields influxQueryFields,
            List<ObjectId> configurationIds
    ) {
        var allSeries = configurationIds
                .stream()
                .flatMap(id -> configurationSeriesCreator
                        .movingAverageConfigurationSeries(fieldName, influxQueryFields, id)
                        .stream()
                )
                .collect(Collectors.toList());

        var bestN = analyticsApi.getBestSeriesApi().nBestSeriesFrom(n, fieldName, optimization, allSeries);
        var bestNAverage = MovingAverageInMemoryOps.movingAverageSeries(bestN, fieldName);

        return new SeriesPack(bestN, bestNAverage);
    }

    public SeriesPack getNBestConfigurationsForExperiment(
            int n,
            String fieldName,
            Optimization optimization,
            InfluxQueryFields influxQueryFields,
            String experimentName
    ) {
        var configurationIds = metadataService.findAllConfigurationIdsForExperiment(experimentName);
        var bestN = getNBestConfigurations(n, fieldName, optimization, influxQueryFields, configurationIds);
        var bestNAverageRenamed = bestN.getAverageSeries().map(s -> s.renamed(experimentName));

        return bestN.withAverageSeries(bestNAverageRenamed);
    }

    public SeriesPack getConfigurationsExceedingThresholds(
            String fieldName,
            Thresholds thresholds,
            CriteriaMode mode,
            InfluxQueryFields influxQueryFields,
            List<ObjectId> configurationIds
    ) {
        var configurationSeries = getConfigurationsSeries(fieldName, influxQueryFields, configurationIds);

        var exceeding = analyticsApi.getThresholdsApi()
                .thresholdsExceedingSeriesFrom(fieldName, thresholds, mode, configurationSeries);
        var exceedingAverage = MovingAverageInMemoryOps.movingAverageSeries(exceeding, fieldName);

        return new SeriesPack(exceeding, exceedingAverage);
    }

    public SeriesPack getConfigurationsExceedingThresholdsForExperiment(
            String fieldName,
            Thresholds thresholds,
            CriteriaMode mode,
            InfluxQueryFields influxQueryFields,
            String experimentName
    ) {
        var configurationIds = metadataService.findAllConfigurationIdsForExperiment(experimentName);
        var exceeding =
                getConfigurationsExceedingThresholds(fieldName, thresholds, mode, influxQueryFields, configurationIds);
        var exceedingAverageRenamed = exceeding.getAverageSeries().map(s -> s.renamed(experimentName));

        return exceeding.withAverageSeries(exceedingAverageRenamed);
    }


    public SeriesPack getConfigurations(
            String fieldName,
            InfluxQueryFields influxQueryFields,
            List<ObjectId> configurationIds
    ) {
        var series = configurationIds.stream()
                .flatMap(id -> configurationSeriesCreator
                        .movingAverageConfigurationSeries(fieldName, influxQueryFields, id)
                        .stream()
                )
                .collect(Collectors.toList());

        var seriesAverage = MovingAverageInMemoryOps
                .movingAverageSeries(series, fieldName);

        return new SeriesPack(series, seriesAverage);
    }

    public SeriesPack getConfigurationsForExperiment(
            String fieldName,
            InfluxQueryFields influxQueryFields,
            String experimentName
    ) {
        var configurationIds = metadataService.findAllConfigurationIdsForExperiment(experimentName);
        var series = getConfigurations(fieldName, influxQueryFields, configurationIds);
        var seriesAverageRenamed = series.getAverageSeries().map(s -> s.renamed(experimentName));

        return series.withAverageSeries(seriesAverageRenamed);
    }

    private Map<ObjectId, DataSeries> getConfigurationsSeries(
            String fieldName,
            InfluxQueryFields influxQueryFields,
            List<ObjectId> configurationsIds
    ) {
        return configurationsIds.stream()
                .flatMap(id -> configurationSeriesCreator
                        .movingAverageConfigurationSeries(fieldName, influxQueryFields, id)
                        .map(configurationSeries -> Tuple.of(id, configurationSeries))
                        .stream()
                )
                .collect(Collectors.toMap(Tuple2::_1, Tuple2::_2));
    }

}
