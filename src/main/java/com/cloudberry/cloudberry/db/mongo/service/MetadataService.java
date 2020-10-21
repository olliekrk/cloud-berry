package com.cloudberry.cloudberry.db.mongo.service;

import com.cloudberry.cloudberry.db.mongo.data.metadata.Experiment;
import com.cloudberry.cloudberry.db.mongo.data.metadata.ExperimentComputation;
import com.cloudberry.cloudberry.db.mongo.data.metadata.ExperimentConfiguration;
import com.cloudberry.cloudberry.db.mongo.service.computation.ComputationsByDifferentIdsService;
import com.cloudberry.cloudberry.db.mongo.service.computation.ExperimentComputationCRUService;
import com.cloudberry.cloudberry.db.mongo.service.configuration.ExperimentConfigurationCRUService;
import com.cloudberry.cloudberry.db.mongo.service.experiment.ExperimentCRUService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MetadataService {
    private final ExperimentConfigurationCRUService experimentConfigurationCRUService;
    private final ExperimentCRUService experimentCRUService;
    private final ExperimentComputationCRUService experimentComputationCRUService;
    private final ComputationsByDifferentIdsService computationsByDifferentIdsService;

    public List<ObjectId> findAllComputationIdsForConfiguration(ObjectId configurationId) {
        return computationsByDifferentIdsService
                .findByConfigurationId(configurationId)
                .map(ExperimentComputation::getId)
                .collectList()
                .block();
    }

    public List<ObjectId> findAllConfigurationIdsForExperiment(String experimentName) {
        return experimentConfigurationCRUService
                .findByExperimentName(experimentName)
                .map(ExperimentConfiguration::getId)
                .collectList()
                .block();
    }

    public Mono<Experiment> getOrCreateExperiment(Experiment experiment) {
        return experimentCRUService.findOrCreateExperiment(experiment);
    }

    public Mono<ExperimentConfiguration> getOrCreateConfiguration(ExperimentConfiguration configuration) {
        return experimentConfigurationCRUService.findOrCreateConfiguration(configuration);
    }

    public Mono<ExperimentComputation> getOrCreateComputation(ExperimentComputation computation) {
        return experimentComputationCRUService.findOrCreateComputation(computation);
    }
}
