package com.cloudberry.cloudberry.db.mongo.service;

import com.cloudberry.cloudberry.common.syntax.MapSyntax;
import com.cloudberry.cloudberry.db.mongo.data.metadata.Experiment;
import com.cloudberry.cloudberry.db.mongo.repository.ExperimentRepository;
import com.cloudberry.cloudberry.db.mongo.service.deletion.ExperimentDeletionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExperimentService {
    private final ExperimentRepository experimentRepository;

    private final ExperimentDeletionService experimentDeletionService;

    public List<Experiment> findAll() {
        return experimentRepository.findAll().collectList().block();
    }

    public List<Experiment> findByName(String name) {
        return experimentRepository.findAllByName(name).collectList().block();
    }

    public Mono<Experiment> getOrCreateExperiment(Experiment experiment) {
        return experimentRepository
                .findById(experiment.getId())
                .switchIfEmpty(experimentRepository
                        .findAllByNameAndParameters(experiment.getName(), experiment.getParameters())
                        .limitRequest(1)
                        .next())
                .doOnNext(next -> log.info("Existing experiment {} was found", next.getId()))
                .switchIfEmpty(saveNewExperiment(experiment));
    }

    public void deleteById(ObjectId experimentId) {
        experimentDeletionService.deleteExperimentById(experimentId).blockLast();
    }

    @NotNull
    private Mono<Experiment> saveNewExperiment(Experiment experiment) {
        log.info("Created new experiment " + experiment.getId());
        return experimentRepository.save(experiment);
    }

    public Experiment update(ObjectId experimentId,
                             @Nullable String name,
                             @Nullable Map<String, Object> newParams,
                             boolean overrideParams) {
        return experimentRepository.findById(experimentId)
                .map(updateExperiment(name, newParams, overrideParams))
                .flatMap(experimentRepository::save)
                .doOnNext(experiment -> log.info("Experiment {} updated", experiment))
                .block();
    }

    @NotNull
    private Function<Experiment, Experiment> updateExperiment(@Nullable String name,
                                                              @Nullable Map<String, Object> newParams,
                                                              boolean overrideParams) {
        return experiment -> {
            val prevParams = experiment.getParameters();
            return experiment
                    .withName(name != null ? name : experiment.getName())
                    .withParameters(newParams != null
                            ? MapSyntax.getNewParamsMap(newParams, prevParams, overrideParams)
                            : prevParams);
        };
    }

}
