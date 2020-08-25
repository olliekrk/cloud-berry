package com.cloudberry.cloudberry.db.mongo.service;

import com.cloudberry.cloudberry.db.mongo.data.metadata.Experiment;
import com.cloudberry.cloudberry.db.mongo.data.metadata.ExperimentComputation;
import com.cloudberry.cloudberry.db.mongo.data.metadata.ExperimentConfiguration;
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
    private final ConfigurationService configurationService;
    private final ExperimentService experimentService;
    private final ComputationService computationService;

    public List<ObjectId> findAllComputationIdsForConfiguration(ObjectId configurationId) {
        return computationService.findAllComputationIdsForConfiguration(configurationId);
    }

    public List<ObjectId> findAllConfigurationIdsForExperiment(String experimentName) {
        return configurationService.findAllForExperimentName(experimentName)
                .stream()
                .map(ExperimentConfiguration::getId)
                .collect(Collectors.toList());
    }

    public Mono<Experiment> getOrCreateExperiment(Experiment experiment) {
        return experimentService.getOrCreateExperiment(experiment);
    }

    public Mono<ExperimentConfiguration> getOrCreateConfiguration(ExperimentConfiguration configuration) {
        return configurationService.getOrCreateConfiguration(configuration);
    }

    public Mono<ExperimentComputation> getOrCreateComputation(ExperimentComputation computation) {
        return computationService.getOrCreateComputation(computation);
    }
}
