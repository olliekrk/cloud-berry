package com.cloudberry.cloudberry.repository;

import com.cloudberry.cloudberry.model.event.ProblemDefinitionEvent;
import com.cloudberry.cloudberry.model.metadata.Experiment;
import com.cloudberry.cloudberry.model.metadata.ExperimentConfiguration;
import com.cloudberry.cloudberry.model.metadata.ExperimentEvaluation;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class MetadataRepositoryFacade {
    private final ExperimentsRepository experimentsRepository;
    private final ConfigurationsRepository configurationsRepository;
    private final EvaluationsRepository evaluationsRepository;

    public MetadataRepositoryFacade(ExperimentsRepository experimentsRepository,
                                    ConfigurationsRepository configurationsRepository,
                                    EvaluationsRepository evaluationsRepository) {
        this.experimentsRepository = experimentsRepository;
        this.configurationsRepository = configurationsRepository;
        this.evaluationsRepository = evaluationsRepository;
    }

    public Mono<Void> saveMetadata(ProblemDefinitionEvent event) {
        var experimentFlux = experimentsRepository.findAllByName(event.name)
                .filter(experiment -> experiment.getParameters().equals(event.experimentParameters))
                .take(1)
                .switchIfEmpty(experimentsRepository.save(new Experiment(event.name, event.experimentParameters)));

        var configurationFlux = experimentFlux.flatMap(experiment -> configurationsRepository.findAllByExperimentId(experiment.getId())
                .filter(configuration -> configuration.getParameters().equals(event.configurationParameters))
                .take(1)
                .switchIfEmpty(configurationsRepository.save(new ExperimentConfiguration(experiment.getId(), event.configurationParameters)))
        );

        // it assumes that there are no other evaluations in DB with the same id
        var evaluationFlux = configurationFlux.flatMap(configuration -> {
            var evaluation = new ExperimentEvaluation(event.evaluationId, configuration.getId(), event.time);
            return evaluationsRepository.save(evaluation);
        });

        return evaluationFlux.then();
    }
}
