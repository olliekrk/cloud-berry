package com.cloudberry.cloudberry.db.mongo.service.computation;

import com.cloudberry.cloudberry.db.mongo.data.metadata.ExperimentComputation;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExperimentComputationService {

    private final ExperimentComputationCRUService experimentComputationCRUService;
    private final ComputationMetaDeletionService computationMetaDeletionService;
    private final ComputationsByDifferentIdsService computationsByDifferentIdsService;

    public List<ExperimentComputation> findAll() {
        return experimentComputationCRUService.findAll().collectList().block();
    }

    public ExperimentComputation findById(ObjectId configurationId) {
        return computationsByDifferentIdsService.findById(configurationId).block();
    }

    public List<ExperimentComputation> findByConfigurationId(ObjectId configurationId) {
        return computationsByDifferentIdsService.findByConfigurationId(configurationId).collectList().block();
    }

    public List<ExperimentComputation> findByExperimentId(ObjectId experimentId) {
        return computationsByDifferentIdsService.findByExperimentId(experimentId).collectList().block();
    }

    public ExperimentComputation getOrCreateComputation(ExperimentComputation computation) {
        return experimentComputationCRUService.findOrCreateComputation(computation).block();
    }

    public void deleteById(ObjectId computationId) {
        computationMetaDeletionService.deleteComputationById(List.of(computationId)).blockLast();
    }
}
