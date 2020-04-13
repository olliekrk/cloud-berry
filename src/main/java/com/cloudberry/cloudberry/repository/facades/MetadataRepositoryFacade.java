package com.cloudberry.cloudberry.repository.facades;

import com.cloudberry.cloudberry.converters.ToMetadataConverter;
import com.cloudberry.cloudberry.model.event.ProblemDefinitionEvent;
import com.cloudberry.cloudberry.model.metadata.ExperimentEvaluation;
import com.cloudberry.cloudberry.repository.ConfigurationsRepository;
import com.cloudberry.cloudberry.repository.EvaluationsRepository;
import com.cloudberry.cloudberry.repository.ExperimentsRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class MetadataRepositoryFacade implements MetadataSaver {
    private final ExperimentsRepository experimentsRepository;
    private final ConfigurationsRepository configurationsRepository;
    private final EvaluationsRepository evaluationsRepository;

    private final ToMetadataConverter toMetadataConverter;

    public MetadataRepositoryFacade(ExperimentsRepository experimentsRepository,
                                    ConfigurationsRepository configurationsRepository,
                                    EvaluationsRepository evaluationsRepository,
                                    ToMetadataConverter toMetadataConverter) {
        this.experimentsRepository = experimentsRepository;
        this.configurationsRepository = configurationsRepository;
        this.evaluationsRepository = evaluationsRepository;
        this.toMetadataConverter = toMetadataConverter;
    }

    @Override
    public Mono<Void> saveMetadata(ProblemDefinitionEvent event) {
        var experimentFlux = experimentsRepository.findAllByName(event.getName())
                .filter(experiment -> experiment.getParameters().equals(event.getExperimentParameters()))
                .take(1)
                .switchIfEmpty(experimentsRepository.save(toMetadataConverter.convert(event)));

        var configurationFlux = experimentFlux.flatMap(experiment -> configurationsRepository.findAllByExperimentId(experiment.getId())
                .filter(configuration -> configuration.getParameters().equals(event.getConfigurationParameters()))
                .take(1)
                .switchIfEmpty(configurationsRepository.save(toMetadataConverter.convert(experiment, event)))
        );

        // it may override other evaluations in DB with the same id
        var evaluationFlux = configurationFlux.flatMap(configuration -> {
            var evaluation = new ExperimentEvaluation(event.getEvaluationId(), configuration.getId(), event.getTime());
            return evaluationsRepository.save(evaluation);
        });

        return evaluationFlux.then();
    }
}
