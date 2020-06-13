package com.cloudberry.cloudberry.repository.facades;

import com.cloudberry.cloudberry.db.mongo.data.metadata.Experiment;
import com.cloudberry.cloudberry.db.mongo.data.metadata.ExperimentConfiguration;
import com.cloudberry.cloudberry.db.mongo.data.metadata.ExperimentEvaluation;
import com.cloudberry.cloudberry.repository.ConfigurationsRepository;
import com.cloudberry.cloudberry.repository.EvaluationsRepository;
import com.cloudberry.cloudberry.repository.ExperimentsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class MetadataRepositoryFacade {

    private final ExperimentsRepository experimentsRepository;
    private final ConfigurationsRepository configurationsRepository;
    private final EvaluationsRepository evaluationsRepository;

    public Mono<Experiment> save(Experiment experiment) {
        return experimentsRepository.findAllByName(experiment.getName())
                .filter(entry -> entry.getParameters().equals(experiment.getParameters()))
                .limitRequest(1)
                .switchIfEmpty(experimentsRepository.save(experiment))
                .next();
    }

    public Mono<ExperimentConfiguration> save(ExperimentConfiguration configuration) {
        return configurationsRepository.findAllByExperimentId(configuration.getExperimentId())
                .filter(entry -> entry.getParameters().equals(configuration.getParameters()))
                .limitRequest(1)
                .switchIfEmpty(configurationsRepository.save(configuration))
                .next();
    }

    public Mono<ExperimentEvaluation> save(ExperimentEvaluation evaluation) {
        return evaluationsRepository.save(evaluation);
    }
}
