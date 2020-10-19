package com.cloudberry.cloudberry.db.mongo.service.experiment;

import com.cloudberry.cloudberry.db.mongo.data.metadata.Experiment;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ExperimentService {
    private final ExperimentMetaDeletionService experimentMetaDeletionService;
    private final ExperimentCRUService experimentCRUService;
    private final ExperimentByDifferentIdsService experimentByDifferentIdsService;

    public List<Experiment> findAll() {
        return experimentCRUService.findAll().collectList().block();
    }

    public Experiment findById(ObjectId experimentId) {
        return experimentByDifferentIdsService.findById(experimentId).block();
    }

    public List<Experiment> findByName(String name) {
        return experimentCRUService.findByName(name).collectList().block();
    }

    public Experiment findByComputationId(ObjectId computationId) {
        return experimentByDifferentIdsService.findByComputationId(computationId).block();
    }

    public Experiment findByConfigurationId(ObjectId configurationId) {
        return experimentByDifferentIdsService.findByConfigurationId(configurationId).block();
    }

    public Experiment findOrCreateExperiment(Experiment experiment) {
        return experimentCRUService.findOrCreateExperiment(experiment).block();
    }

    public Experiment update(ObjectId experimentId,
                             @Nullable String name,
                             @Nullable Map<String, Object> newParams,
                             boolean overrideParams) {
        return experimentCRUService.update(experimentId, name, newParams, overrideParams).block();
    }

    public void deleteById(List<ObjectId> experimentIds) {
        experimentMetaDeletionService.deleteExperimentById(experimentIds).blockLast();
    }

}
