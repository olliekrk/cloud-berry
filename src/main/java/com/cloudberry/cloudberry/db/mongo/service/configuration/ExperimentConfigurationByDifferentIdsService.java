package com.cloudberry.cloudberry.db.mongo.service.configuration;

import com.cloudberry.cloudberry.db.mongo.data.metadata.ExperimentComputation;
import com.cloudberry.cloudberry.db.mongo.data.metadata.ExperimentConfiguration;
import com.cloudberry.cloudberry.db.mongo.repository.ComputationRepository;
import com.cloudberry.cloudberry.db.mongo.repository.ConfigurationRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ExperimentConfigurationByDifferentIdsService {
    private final ConfigurationRepository configurationRepository;
    private final ComputationRepository computationRepository;

    public Mono<ExperimentConfiguration> findByComputationId(ObjectId computationId) {
        return computationRepository.findById(computationId)
                .map(ExperimentComputation::getConfigurationId)
                .flatMap(configurationRepository::findById);
    }

    public Mono<ExperimentConfiguration> findById(ObjectId configurationId) {
        return configurationRepository.findById(configurationId);
    }

    public Flux<ExperimentConfiguration> findByExperimentId(ObjectId experimentId) {
        return configurationRepository.findAllByExperimentId(experimentId);
    }
}
