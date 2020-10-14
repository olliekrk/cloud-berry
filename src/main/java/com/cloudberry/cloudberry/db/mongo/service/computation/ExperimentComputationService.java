package com.cloudberry.cloudberry.db.mongo.service.computation;

import com.cloudberry.cloudberry.db.mongo.data.metadata.ExperimentComputation;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExperimentComputationService {

    private final ExperimentComputationCRUService experimentComputationCRUService;
    private final ComputationMetaDeletionService computationMetaDeletionService;

    public List<ExperimentComputation> findAll() {
        return experimentComputationCRUService.findAll();
    }

    public List<ExperimentComputation> findAllComputationsForConfigurationId(ObjectId configurationId) {
        return experimentComputationCRUService.findAllComputationsForConfigurationId(configurationId);
    }

    public Mono<ExperimentComputation> getOrCreateComputation(ExperimentComputation computation) {
        return experimentComputationCRUService.getOrCreateComputation(computation);
    }

    public void deleteById(ObjectId computationId) {
        computationMetaDeletionService.deleteComputationById(List.of(computationId)).blockLast();
    }
}
