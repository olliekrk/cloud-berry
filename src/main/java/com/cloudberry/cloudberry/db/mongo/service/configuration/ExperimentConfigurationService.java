package com.cloudberry.cloudberry.db.mongo.service.configuration;

import com.cloudberry.cloudberry.db.mongo.data.metadata.ExperimentConfiguration;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ExperimentConfigurationService {

    private final ExperimentConfigurationCRUService experimentConfigurationCRUService;
    private final ExperimentConfigurationDeletionService experimentConfigurationDeletionService;

    public List<ExperimentConfiguration> findAll() {
        return experimentConfigurationCRUService.findAll();
    }

    public List<ExperimentConfiguration> findAllForConfigurationFileName(String configurationFileName) {
        return experimentConfigurationCRUService.findAllForConfigurationFileName(configurationFileName);
    }

    public List<ExperimentConfiguration> findAllForExperimentName(String experimentName) {
        return experimentConfigurationCRUService.findAllForExperimentName(experimentName);
    }

    public Mono<ExperimentConfiguration> getOrCreateConfiguration(ExperimentConfiguration configuration) {
        return experimentConfigurationCRUService.getOrCreateConfiguration(configuration);
    }

    public ExperimentConfiguration update(ObjectId configurationId,
                                          @Nullable String configurationFileName,
                                          @Nullable Map<String, Object> newParams,
                                          boolean overrideParams) {
        return experimentConfigurationCRUService.update(configurationId, configurationFileName, newParams,
                overrideParams);
    }

    public void deleteById(ObjectId configurationId) {
        experimentConfigurationDeletionService.deleteConfigurationById(configurationId).blockLast();
    }

}
