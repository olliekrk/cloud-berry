package com.cloudberry.cloudberry.db.mongo.service.configuration.delete;

import com.cloudberry.cloudberry.EmbeddedMongoTest;
import com.cloudberry.cloudberry.db.mongo.repository.ComputationRepository;
import com.cloudberry.cloudberry.db.mongo.repository.ConfigurationRepository;
import com.cloudberry.cloudberry.db.mongo.service.DataGenerator;
import com.cloudberry.cloudberry.db.mongo.service.computation.ComputationMetaDeletionService;
import com.cloudberry.cloudberry.db.mongo.service.configuration.ExperimentConfigurationMetaDeletionService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.stream.Stream;

@Import({ExperimentConfigurationMetaDeletionService.class, ComputationMetaDeletionService.class})
@EmbeddedMongoTest
abstract class ConfigurationDeletionServiceTestBase {
    @Autowired
    protected ExperimentConfigurationMetaDeletionService configurationDeletionService;

    @Autowired
    protected ConfigurationRepository configurationRepository;
    @Autowired
    protected ComputationRepository computationRepository;

    @BeforeEach
    void cleanDatabase() {
        Stream.of(configurationRepository, computationRepository)
                .map(ReactiveCrudRepository::deleteAll)
                .forEach(Mono::block);
    }

    protected void saveAllToDatabase() {
        Stream.of(
                configurationRepository.saveAll(DataGenerator.ALL_CONFIGURATIONS),
                computationRepository.saveAll(DataGenerator.ALL_COMPUTATIONS)
        )
                .forEach(Flux::blockLast);
    }

}