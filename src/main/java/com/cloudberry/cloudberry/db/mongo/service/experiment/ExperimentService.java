package com.cloudberry.cloudberry.db.mongo.service.experiment;

import com.cloudberry.cloudberry.db.mongo.data.metadata.Experiment;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ExperimentService {
    private final ExperimentDeletionService experimentDeletionService;
    private final ExperimentCRUService experimentCRUService;

    public List<Experiment> findAll() {
        return experimentCRUService.findAll();
    }

    public List<Experiment> findByName(String name) {
        return experimentCRUService.findByName(name);
    }

    public Mono<Experiment> getOrCreateExperiment(Experiment experiment) {
        return experimentCRUService.getOrCreateExperiment(experiment);
    }

    public Experiment update(ObjectId experimentId,
                             @Nullable String name,
                             @Nullable Map<String, Object> newParams,
                             boolean overrideParams) {
        return experimentCRUService.update(experimentId, name, newParams, overrideParams);
    }

    public void deleteById(ObjectId experimentId) {
        experimentDeletionService.deleteExperimentById(experimentId).blockLast();
    }

}
