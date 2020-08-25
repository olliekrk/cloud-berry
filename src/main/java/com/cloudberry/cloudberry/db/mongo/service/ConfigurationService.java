package com.cloudberry.cloudberry.db.mongo.service;

import com.cloudberry.cloudberry.db.mongo.data.metadata.Experiment;
import com.cloudberry.cloudberry.db.mongo.data.metadata.ExperimentConfiguration;
import com.cloudberry.cloudberry.db.mongo.repository.ConfigurationsRepository;
import com.cloudberry.cloudberry.db.mongo.repository.ExperimentsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConfigurationService {
    private final ConfigurationsRepository configurationsRepository;
    private final ExperimentsRepository experimentsRepository;

    public List<ExperimentConfiguration> findAll() {
        return configurationsRepository.findAll().collectList().block();
    }

    public List<ExperimentConfiguration> findAllForConfigurationFileName(String configurationFileName) {
        return configurationsRepository.findAllByConfigurationFileName(configurationFileName).collectList().block();
    }

    public List<ExperimentConfiguration> findAllForExperimentName(String experimentName) {
        return experimentsRepository.findAllByName(experimentName)
                .map(Experiment::getId)
                .flatMap(configurationsRepository::findAllByExperimentId)
                .collectList()
                .block();
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
}
