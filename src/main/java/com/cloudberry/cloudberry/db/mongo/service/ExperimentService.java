package com.cloudberry.cloudberry.db.mongo.service;

import com.cloudberry.cloudberry.common.syntax.MapSyntax;
import com.cloudberry.cloudberry.db.mongo.data.metadata.Experiment;
import com.cloudberry.cloudberry.db.mongo.repository.ExperimentsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.bson.types.ObjectId;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExperimentService {
    private final ExperimentsRepository experimentsRepository;

    public List<Experiment> findAll() {
        return experimentsRepository.findAll().collectList().block();
    }

    public List<Experiment> findByName(String name) {
        return experimentsRepository.findAllByName(name).collectList().block();
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
                .doOnNext(next1 -> log.info("Existing experiment " + next1.getId() + " was found"))
                .switchIfEmpty(experimentsRepository.save(experiment))
                .doOnNext(next -> log.info("Created new experiment " + next.getId()));
    }

    public Experiment update(ObjectId experimentId,
                             @Nullable String name,
                             @Nullable Map<String, Object> newParams,
                             boolean overrideParams) {
        return experimentsRepository.findById(experimentId)
                .doOnNext(experiment -> {
                    val prevParams = experiment.getParameters();
                    val updatedExperiment = experiment
                            .withName(name != null ? name : experiment.getName())
                            .withParameters(newParams != null
                                    ? getNewParamsMap(newParams, prevParams, overrideParams)
                                    : prevParams);
                    experimentsRepository.save(updatedExperiment);
                }).block();
    }

    private Map<String, Object> getNewParamsMap(Map<String, Object> newParams,
                                                Map<String, Object> prevParams,
                                                boolean overrideParams) {
        return overrideParams ? newParams : MapSyntax.merged(prevParams, newParams);
    }

}
