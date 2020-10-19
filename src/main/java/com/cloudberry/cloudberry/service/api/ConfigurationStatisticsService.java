package com.cloudberry.cloudberry.service.api;

import com.cloudberry.cloudberry.analytics.AnalyticsApi;
import com.cloudberry.cloudberry.analytics.model.CriteriaMode;
import com.cloudberry.cloudberry.analytics.model.DataSeries;
import com.cloudberry.cloudberry.analytics.model.InfluxQueryFields;
import com.cloudberry.cloudberry.analytics.model.optimization.Optimization;
import com.cloudberry.cloudberry.analytics.model.thresholds.Thresholds;
import com.cloudberry.cloudberry.common.syntax.ListSyntax;
import com.cloudberry.cloudberry.db.mongo.service.MetadataService;
import com.cloudberry.cloudberry.service.configurations.ConfigurationSeriesCreator;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConfigurationStatisticsService {
    private final AnalyticsApi analyticsApi;
    private final MetadataService metadataService;
    private final ConfigurationSeriesCreator configurationSeriesCreator;

    public List<DataSeries> getNBestConfigurations(
            int n,
            String fieldName,
            Optimization optimization,
            InfluxQueryFields influxQueryFields,
            List<ObjectId> configurationIds
    ) {
        return analyticsApi.getBestConfigurationsApi().nBestConfigurations(
                n,
                fieldName,
                optimization,
                influxQueryFields,
                configurationIds
        );
    }

    public List<DataSeries> getNBestConfigurationsForExperiment(
            int n,
            String fieldName,
            Optimization optimization,
            InfluxQueryFields influxQueryFields,
            String experimentName
    ) {
        return getNBestConfigurations(
                n,
                fieldName,
                optimization,
                influxQueryFields,
                metadataService.findAllConfigurationIdsForExperiment(experimentName)
        );
    }

    public List<DataSeries> getConfigurationsExceedingThresholds(
            String fieldName,
            Thresholds thresholds,
            CriteriaMode mode,
            InfluxQueryFields influxQueryFields,
            List<ObjectId> configurationIds
    ) {
        return analyticsApi.getThresholdsApi().thresholdsExceedingSeriesFrom(
                fieldName,
                thresholds,
                mode,
                getConfigurationsSeries(fieldName, influxQueryFields, configurationIds)
        );
    }

    public List<DataSeries> getConfigurationsExceedingThresholdsForExperiment(
            String fieldName,
            Thresholds thresholds,
            CriteriaMode mode,
            InfluxQueryFields influxQueryFields,
            String experimentName
    ) {
        return getConfigurationsExceedingThresholds(
                fieldName,
                thresholds,
                mode,
                influxQueryFields,
                metadataService.findAllConfigurationIdsForExperiment(experimentName)
        );
    }


    public List<DataSeries> getConfigurationsMeans(String fieldName,
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

    public List<DataSeries> getConfigurationsMeansForExperiment(String fieldName,
                                                                InfluxQueryFields influxQueryFields,
                                                                String experimentName) {
        var configurationIds = metadataService.findAllConfigurationIdsForExperiment(experimentName);
        return getConfigurationsMeans(fieldName, influxQueryFields, configurationIds);
    }

    private Map<ObjectId, DataSeries> getConfigurationsSeries(String fieldName,
                                                              InfluxQueryFields influxQueryFields,
                                                              List<ObjectId> configurationsIds) {
        return configurationsIds.stream().collect(Collectors.toMap(
                Function.identity(),
                id -> configurationSeriesCreator.createMovingAverageConfigurationSeries(
                        fieldName,
                        influxQueryFields,
                        id
                )
        ));
    }

}
