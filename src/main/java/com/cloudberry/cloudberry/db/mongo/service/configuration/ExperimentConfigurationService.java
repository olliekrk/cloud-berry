package com.cloudberry.cloudberry.db.mongo.service.configuration;

import com.cloudberry.cloudberry.db.mongo.data.metadata.ExperimentConfiguration;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ExperimentConfigurationService {

    private final ExperimentConfigurationCRUService experimentConfigurationCRUService;
    private final ExperimentConfigurationByDifferentIdsService experimentConfigurationByDifferentIdsService;
    private final ExperimentConfigurationMetaDeletionService experimentConfigurationMetaDeletionService;

    public List<ExperimentConfiguration> findAll() {
        return experimentConfigurationCRUService.findAll().collectList().block();
    }

    public ExperimentConfiguration findById(ObjectId configurationId) {
        return experimentConfigurationByDifferentIdsService.findById(configurationId).block();
    }

    public List<ExperimentConfiguration> findByConfigurationFileName(String configurationFileName) {
        return experimentConfigurationCRUService.findByConfigurationFileName(configurationFileName).collectList()
                .block();
    }

    public List<ExperimentConfiguration> findByExperimentName(String experimentName) {
        return experimentConfigurationCRUService.findByExperimentName(experimentName).collectList().block();
    }

    public List<ExperimentConfiguration> findByExperimentId(ObjectId experimentId) {
        return experimentConfigurationByDifferentIdsService.findByExperimentId(experimentId).collectList().block();
    }

    public ExperimentConfiguration findByComputationId(ObjectId computationId) {
        return experimentConfigurationByDifferentIdsService.findByComputationId(computationId).block();
    }

    public ExperimentConfiguration getOrCreateConfiguration(ExperimentConfiguration configuration) {
        return experimentConfigurationCRUService.getOrCreateConfiguration(configuration).block();
    }

    public ExperimentConfiguration update(
            ObjectId configurationId,
            @Nullable String configurationFileName,
            @Nullable Map<String, Object> newParams,
            boolean overrideParams
    ) {
        return experimentConfigurationCRUService
                .update(configurationId, configurationFileName, newParams, overrideParams)
                .block();
    }

    public void deleteById(ObjectId configurationId) {
        experimentConfigurationMetaDeletionService.deleteConfigurationById(List.of(configurationId)).blockLast();
    }

}
