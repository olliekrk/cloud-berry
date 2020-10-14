package com.cloudberry.cloudberry.db.mongo.service.experiment;

import com.cloudberry.cloudberry.db.mongo.data.metadata.Experiment;
import com.cloudberry.cloudberry.db.mongo.data.metadata.ExperimentConfiguration;
import com.cloudberry.cloudberry.db.mongo.repository.ConfigurationRepository;
import com.cloudberry.cloudberry.db.mongo.repository.ExperimentRepository;
import com.cloudberry.cloudberry.db.mongo.service.configuration.ExperimentConfigurationByDifferentIdsService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ExperimentByDifferentIdsService {
    private final ExperimentConfigurationByDifferentIdsService experimentConfigurationByDifferentIdsService;
    private final ExperimentRepository experimentRepository;
    private final ConfigurationRepository configurationRepository;

    public Mono<Experiment> findByComputationId(ObjectId computationId) {
        return experimentConfigurationByDifferentIdsService
                .findByComputationId(computationId)
                .map(ExperimentConfiguration::getExperimentId)
                .flatMap(experimentRepository::findById);
    }

    public Mono<Experiment> findByConfigurationId(ObjectId configurationId) {
        return configurationRepository
                .findById(configurationId)
                .map(ExperimentConfiguration::getExperimentId)
                .flatMap(experimentRepository::findById);
    }

    public Mono<Experiment> findById(ObjectId experimentId) {
        return experimentRepository.findById(experimentId);
    }

}