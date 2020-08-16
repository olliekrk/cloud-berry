package com.cloudberry.cloudberry.db.mongo.service;

import com.cloudberry.cloudberry.db.mongo.data.metadata.Experiment;
import com.cloudberry.cloudberry.db.mongo.data.metadata.ExperimentConfiguration;
import com.cloudberry.cloudberry.db.mongo.data.metadata.ExperimentEvaluation;
import com.cloudberry.cloudberry.db.mongo.repository.ConfigurationsRepository;
import com.cloudberry.cloudberry.db.mongo.repository.EvaluationsRepository;
import com.cloudberry.cloudberry.db.mongo.repository.ExperimentsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MetadataService {
    private final ConfigurationsRepository configurationsRepository;
    private final EvaluationsRepository evaluationsRepository;
    private final ExperimentsRepository experimentsRepository;

    public List<ObjectId> findAllEvaluationIdsForConfiguration(ObjectId configurationId) {
        return evaluationsRepository.findAllByConfigurationId(configurationId)
                .map(ExperimentEvaluation::getId)
                .collectList()
                .block();
    }

    public List<ObjectId> findAllConfigurationIdsForExperiment(String experimentName) {
        return experimentsRepository.findAllByName(experimentName)
                .map(Experiment::getId)
                .flatMap(configurationsRepository::findAllByExperimentId)
                .map(ExperimentConfiguration::getId)
                .collectList()
                .block();
    }

    public Mono<Experiment> getOrCreateExperiment(Experiment experiment) {
        return experimentsRepository
                .findById(experiment.getId())
                .switchIfEmpty(
                        experimentsRepository.findAllByName(experiment.getName())
                                .filter(existing -> existing.getParameters().equals(experiment.getParameters()))
                                .limitRequest(1)
                                .next()
                )
                .doOnNext(next -> log.info("Existing experiment " + next.getId() + " was found"))
                .switchIfEmpty(experimentsRepository.save(experiment))
                .doOnNext(next -> log.info("Created new experiment " + next.getId()));
    }

    public Mono<ExperimentConfiguration> getOrCreateConfiguration(ExperimentConfiguration configuration) {
        return configurationsRepository
                .findById(configuration.getId())
                .switchIfEmpty(
                        configurationsRepository.findAllByExperimentId(configuration.getExperimentId())
                                .filter(existing -> existing.getParameters().equals(configuration.getParameters()))
                                .limitRequest(1)
                                .next()
                )
                .doOnNext(next -> log.info("Existing configuration " + next.getId() + " was found"))
                .switchIfEmpty(configurationsRepository.save(configuration))
                .doOnNext(next -> log.info("Created new configuration " + next.getId()));
    }

    public Mono<ExperimentEvaluation> getOrCreateEvaluation(ExperimentEvaluation evaluation) {
        return evaluationsRepository
                .findById(evaluation.getId())
                .doOnNext(next -> log.info("Existing evaluation " + next.getId() + " was found"))
                .switchIfEmpty(evaluationsRepository.save(evaluation))
                .doOnNext(next -> log.info("Created new evaluation " + next.getId()));
    }

}
