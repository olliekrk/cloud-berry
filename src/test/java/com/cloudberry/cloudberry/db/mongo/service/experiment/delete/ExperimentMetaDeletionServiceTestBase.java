package com.cloudberry.cloudberry.db.mongo.service.experiment.delete;

import com.cloudberry.cloudberry.EmbeddedMongoTest;
import com.cloudberry.cloudberry.db.mongo.repository.ComputationRepository;
import com.cloudberry.cloudberry.db.mongo.repository.ConfigurationRepository;
import com.cloudberry.cloudberry.db.mongo.repository.ExperimentRepository;
import com.cloudberry.cloudberry.db.mongo.service.DataGenerator;
import com.cloudberry.cloudberry.db.mongo.service.computation.ComputationMetaDeletionService;
import com.cloudberry.cloudberry.db.mongo.service.configuration.ExperimentConfigurationMetaDeletionService;
import com.cloudberry.cloudberry.db.mongo.service.experiment.ExperimentMetaDeletionService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.stream.Stream;

@Import({ExperimentMetaDeletionService.class, ExperimentConfigurationMetaDeletionService.class,
        ComputationMetaDeletionService.class})
@EmbeddedMongoTest
abstract class ExperimentMetaDeletionServiceTestBase {
    @Autowired
    protected ExperimentMetaDeletionService experimentMetaDeletionService;

    @Autowired
    protected ExperimentRepository experimentRepository;
    @Autowired
    protected ConfigurationRepository configurationRepository;
    @Autowired
    protected ComputationRepository computationRepository;

    @BeforeEach
    void cleanDatabase() {
        Stream.of(experimentRepository, configurationRepository, computationRepository)
                .map(ReactiveCrudRepository::deleteAll)
                .forEach(Mono::block);
    }

    protected void saveAllToDatabase() {
        Stream.of(
                experimentRepository.saveAll(DataGenerator.ALL_EXPERIMENTS),
                configurationRepository.saveAll(DataGenerator.ALL_CONFIGURATIONS),
                computationRepository.saveAll(DataGenerator.ALL_COMPUTATIONS)
        )
                .forEach(Flux::blockLast);
    }

}