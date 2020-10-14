package com.cloudberry.cloudberry.db.mongo.service.experiment;

import com.cloudberry.cloudberry.common.syntax.MapSyntax;
import com.cloudberry.cloudberry.db.mongo.data.metadata.Experiment;
import com.cloudberry.cloudberry.db.mongo.repository.ExperimentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.function.Function;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExperimentCRUService {
    private final ExperimentRepository experimentRepository;

    public Flux<Experiment> findAll() {
        return experimentRepository.findAll();
    }

    public Flux<Experiment> findByName(String name) {
        return experimentRepository.findAllByName(name);
    }

    public Mono<Experiment> findOrCreateExperiment(Experiment experiment) {
        return experimentRepository
                .findById(experiment.getId())
                .switchIfEmpty(experimentRepository
                        .findAllByNameAndParameters(experiment.getName(), experiment.getParameters())
                        .limitRequest(1)
                        .next())
                .doOnNext(next -> log.info("Existing experiment {} was found", next.getId()))
                .switchIfEmpty(saveNewExperiment(experiment));
    }

    public Mono<Experiment> update(ObjectId experimentId,
                                   @Nullable String name,
                                   @Nullable Map<String, Object> newParams,
                                   boolean overrideParams) {
        return experimentRepository.findById(experimentId)
                .map(updateExperiment(name, newParams, overrideParams))
                .flatMap(experimentRepository::save)
                .doOnNext(experiment -> log.info("Experiment {} updated", experiment));
    }

    @NotNull
    private Mono<Experiment> saveNewExperiment(Experiment experiment) {
        log.info("Created new experiment " + experiment.getId());
        return experimentRepository.save(experiment);
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
