package com.cloudberry.cloudberry.db.mongo.service;

import com.cloudberry.cloudberry.common.syntax.MapSyntax;
import com.cloudberry.cloudberry.db.mongo.data.metadata.Experiment;
import com.cloudberry.cloudberry.db.mongo.data.metadata.ExperimentConfiguration;
import com.cloudberry.cloudberry.db.mongo.repository.ConfigurationRepository;
import com.cloudberry.cloudberry.db.mongo.repository.ExperimentRepository;
import com.cloudberry.cloudberry.db.mongo.service.deletion.ConfigurationDeletionService;
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
public class ExperimentConfigurationService {
    private final ExperimentRepository experimentRepository;
    private final ConfigurationRepository configurationRepository;

    private final ConfigurationDeletionService configurationDeletionService;

    public List<ExperimentConfiguration> findAll() {
        return configurationRepository.findAll().collectList().block();
    }

    public List<ExperimentConfiguration> findAllForConfigurationFileName(String configurationFileName) {
        return configurationRepository.findAllByConfigurationFileName(configurationFileName).collectList().block();
    }

    public List<ExperimentConfiguration> findAllForExperimentName(String experimentName) {
        return experimentRepository.findAllByName(experimentName)
                .map(Experiment::getId)
                .flatMap(configurationRepository::findAllByExperimentId)
                .collectList()
                .block();
    }

    public Mono<ExperimentConfiguration> getOrCreateConfiguration(ExperimentConfiguration configuration) {
        return configurationRepository
                .findById(configuration.getId())
                .switchIfEmpty(
                        configurationRepository.findAllByExperimentId(configuration.getExperimentId())
                                .filter(existing -> existing.getParameters().equals(configuration.getParameters()))
                                .limitRequest(1)
                                .next()
                )
                .doOnNext(next -> log.info("Existing configuration " + next.getId() + " was found"))
                .switchIfEmpty(configurationRepository.save(configuration))
                .doOnNext(next -> log.info("Created new configuration " + next.getId()));
    }

    public ExperimentConfiguration update(ObjectId configurationId,
                                          @Nullable String configurationFileName,
                                          @Nullable Map<String, Object> newParams,
                                          boolean overrideParams) {
        return configurationRepository.findById(configurationId)
                .map(updateConfiguration(configurationFileName, newParams, overrideParams))
                .flatMap(configurationRepository::save)
                .doOnNext(experiment -> log.info("Experiment configuration {} updated", experiment))
                .block();
    }

    public void deleteById(ObjectId configurationId) {
        configurationDeletionService.deleteConfigurationById(configurationId).blockLast();
    }

    @NotNull
    private Function<ExperimentConfiguration, ExperimentConfiguration> updateConfiguration(
            @Nullable String configurationFileName,
            @Nullable Map<String, Object> newParams,
            boolean overrideParams) {
        return configuration -> {
            val prevParams = configuration.getParameters();
            return configuration
                    .withConfigurationFileName(
                            configurationFileName != null
                                    ? configurationFileName
                                    : configuration.getConfigurationFileName())
                    .withParameters(newParams != null
                            ? MapSyntax.getNewParamsMap(newParams, prevParams, overrideParams)
                            : prevParams);
        };
    }


}
