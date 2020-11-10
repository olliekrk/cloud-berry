package com.cloudberry.cloudberry.service.configurations;

import com.cloudberry.cloudberry.db.mongo.data.metadata.ExperimentConfiguration;
import com.cloudberry.cloudberry.db.mongo.service.configuration.ExperimentConfigurationByDifferentIdsService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConfigurationSeriesNameResolver {
    private final ExperimentConfigurationByDifferentIdsService experimentService;

    public String configurationSeriesName(ObjectId configurationId) {
        return experimentService.findById(configurationId)
                .blockOptional()
                .map(ExperimentConfiguration::getConfigurationName)
                .orElse(String.format("configuration_%s", configurationId.toHexString()));
    }

    public String configurationSeriesName(ExperimentConfiguration configuration) {
        return configuration.getConfigurationName();
    }
}
