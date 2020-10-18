package com.cloudberry.cloudberry.service.api;

import com.cloudberry.cloudberry.analytics.AnalyticsApi;
import com.cloudberry.cloudberry.analytics.model.DataSeries;
import com.cloudberry.cloudberry.analytics.model.InfluxQueryFields;
import com.cloudberry.cloudberry.analytics.model.optimization.Optimization;
import com.cloudberry.cloudberry.common.syntax.ListSyntax;
import com.cloudberry.cloudberry.db.mongo.service.MetadataService;
import com.cloudberry.cloudberry.service.configurations.ConfigurationSeriesCreator;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.List;

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

}
