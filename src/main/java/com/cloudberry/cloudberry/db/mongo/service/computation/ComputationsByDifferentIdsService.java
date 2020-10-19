package com.cloudberry.cloudberry.db.mongo.service.computation;

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
public class ComputationsByDifferentIdsService {
    private final ConfigurationRepository configurationRepository;
    private final ComputationRepository computationRepository;

    public Mono<ExperimentComputation> findById(ObjectId computationId) {
        return computationRepository.findById(computationId);
    }

    public Flux<ExperimentComputation> findByConfigurationId(ObjectId configurationId) {
        return Flux.from(computationRepository.findAllByConfigurationId(configurationId));
    }

    public Flux<ExperimentComputation> findByExperimentId(ObjectId experimentId) {
        return configurationRepository.findAllByExperimentId(experimentId)
                .map(ExperimentConfiguration::getId)
                .flatMap(computationRepository::findAllByConfigurationId);
    }

}
