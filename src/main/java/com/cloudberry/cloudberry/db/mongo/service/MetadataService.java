package com.cloudberry.cloudberry.db.mongo.service;

import com.cloudberry.cloudberry.db.mongo.data.metadata.Experiment;
import com.cloudberry.cloudberry.db.mongo.data.metadata.ExperimentComputation;
import com.cloudberry.cloudberry.db.mongo.data.metadata.ExperimentConfiguration;
import com.cloudberry.cloudberry.db.mongo.service.computation.ExperimentComputationCRUService;
import com.cloudberry.cloudberry.db.mongo.service.experiment.ExperimentCRUService;
import com.cloudberry.cloudberry.db.mongo.service.configuration.ExperimentConfigurationCRUService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MetadataService {
    private final ExperimentConfigurationCRUService experimentConfigurationCRUService;
    private final ExperimentCRUService experimentCRUService;
    private final ExperimentComputationCRUService experimentComputationCRUService;

    public List<ObjectId> findAllComputationIdsForConfiguration(ObjectId configurationId) {
        return experimentComputationCRUService.findAllComputationsForConfigurationId(configurationId)
                .stream()
                .map(ExperimentComputation::getId)
                .collect(Collectors.toList());
    }

    public List<ObjectId> findAllConfigurationIdsForExperiment(String experimentName) {
        return experimentConfigurationCRUService.findAllForExperimentName(experimentName)
                .stream()
                .map(ExperimentConfiguration::getId)
                .collect(Collectors.toList());
    }

    public Mono<Experiment> getOrCreateExperiment(Experiment experiment) {
        return experimentCRUService.getOrCreateExperiment(experiment);
    }

    public Mono<ExperimentConfiguration> getOrCreateConfiguration(ExperimentConfiguration configuration) {
        return experimentConfigurationCRUService.getOrCreateConfiguration(configuration);
    }

    public Mono<ExperimentComputation> getOrCreateComputation(ExperimentComputation computation) {
        return experimentComputationCRUService.getOrCreateComputation(computation);
    }
}
