package com.cloudberry.cloudberry.service.configurations;

import com.cloudberry.cloudberry.analytics.api.SeriesApi;
import com.cloudberry.cloudberry.analytics.model.basic.DataSeries;
import com.cloudberry.cloudberry.analytics.model.query.InfluxQueryFields;
import com.cloudberry.cloudberry.analytics.service.average.moving.MovingAverageInMemoryOps;
import com.cloudberry.cloudberry.db.mongo.service.MetadataService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ConfigurationSeriesCreator {
    private final SeriesApi seriesApi;
    private final MetadataService metadataService;
    private final ConfigurationSeriesNameResolver configurationSeriesNameResolver;

    public Optional<DataSeries> movingAverageConfigurationSeries(
            String fieldName,
            InfluxQueryFields influxQueryFields,
            ObjectId configurationId
    ) {
        var computationsIds = metadataService.findAllComputationIdsForConfiguration(configurationId);
        var computationsSeries = seriesApi.computationsSeries(fieldName, computationsIds, influxQueryFields);

        return MovingAverageInMemoryOps
                .movingAverageSeries(computationsSeries, fieldName)
                .map(s -> s.withSeriesName(configurationSeriesNameResolver.configurationSeriesName(configurationId)));
    }

}
