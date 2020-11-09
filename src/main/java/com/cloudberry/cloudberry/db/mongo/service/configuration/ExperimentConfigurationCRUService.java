package com.cloudberry.cloudberry.db.mongo.service.configuration;

import com.cloudberry.cloudberry.common.syntax.MapSyntax;
import com.cloudberry.cloudberry.db.mongo.data.metadata.Experiment;
import com.cloudberry.cloudberry.db.mongo.data.metadata.ExperimentConfiguration;
import com.cloudberry.cloudberry.db.mongo.repository.ConfigurationRepository;
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
public class ExperimentConfigurationCRUService {
    private final ExperimentRepository experimentRepository;
    private final ConfigurationRepository configurationRepository;

    public Flux<ExperimentConfiguration> findAll() {
        return configurationRepository.findAll();
    }

    public Flux<ExperimentConfiguration> findByConfigurationName(String configurationName) {
        return configurationRepository.findAllByConfigurationName(configurationName);
    }

    public Flux<ExperimentConfiguration> findByExperimentName(String experimentName) {
        return experimentRepository.findAllByName(experimentName)
                .map(Experiment::getId)
                .flatMap(configurationRepository::findAllByExperimentId);
    }

    public Mono<ExperimentConfiguration> findOrCreateConfiguration(ExperimentConfiguration configuration) {
        return configurationRepository
                .findById(configuration.getId())
                .switchIfEmpty(findExistingConfiguration(configuration))
                .switchIfEmpty(saveNewConfiguration(configuration));
    }

    public Mono<ExperimentConfiguration> update(
            ObjectId configurationId,
            @Nullable String configurationName,
            @Nullable Map<String, Object> newParams,
            boolean overrideParams
    ) {
        return configurationRepository.findById(configurationId)
                .map(updateConfiguration(configurationName, newParams, overrideParams))
                .flatMap(configurationRepository::save)
                .doOnNext(experiment -> log.info("Experiment configuration {} updated", experiment));
    }

    @NotNull
    private Function<ExperimentConfiguration, ExperimentConfiguration> updateConfiguration(
            @Nullable String configurationName,
            @Nullable Map<String, Object> newParams,
            boolean overrideParams
    ) {
        return configuration -> {
            val prevParams = configuration.getParameters();
            return configuration
                    .withConfigurationName(
                            configurationName != null
                                    ? configurationName
                                    : configuration.getConfigurationName())
                    .withParameters(newParams != null
                                            ? MapSyntax.getNewParamsMap(newParams, prevParams, overrideParams)
                                            : prevParams);
        };
    }

    private Mono<ExperimentConfiguration> saveNewConfiguration(
            ExperimentConfiguration configuration
    ) {
        return configurationRepository.save(configuration)
                .doOnNext(_e -> log.info("Created new configuration " + configuration.getId()));
    }

    private Mono<ExperimentConfiguration> findExistingConfiguration(
            ExperimentConfiguration configuration
    ) {
        return configurationRepository.findAllByExperimentId(configuration.getExperimentId())
                .filter(other -> other.getParameters().equals(configuration.getParameters()))
                .limitRequest(1)
                .next()
                .doOnNext(next -> log.info("Existing configuration " + next.getId() + " was found"));
    }

}
