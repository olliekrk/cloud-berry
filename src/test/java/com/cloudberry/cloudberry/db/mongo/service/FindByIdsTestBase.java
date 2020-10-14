package com.cloudberry.cloudberry.db.mongo.service;

import com.cloudberry.cloudberry.EmbeddedMongoTest;
import com.cloudberry.cloudberry.db.mongo.repository.ComputationRepository;
import com.cloudberry.cloudberry.db.mongo.repository.ConfigurationRepository;
import com.cloudberry.cloudberry.db.mongo.repository.ExperimentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.stream.Stream;

import static com.cloudberry.cloudberry.db.mongo.service.DataGenerator.ALL_COMPUTATIONS;
import static com.cloudberry.cloudberry.db.mongo.service.DataGenerator.ALL_CONFIGURATIONS;
import static com.cloudberry.cloudberry.db.mongo.service.DataGenerator.ALL_EXPERIMENTS;

@EmbeddedMongoTest
public class FindByIdsTestBase {
    @Autowired
    protected ExperimentRepository experimentRepository;
    @Autowired
    protected ConfigurationRepository configurationRepository;
    @Autowired
    protected ComputationRepository computationRepository;

    @BeforeEach
    void prepareDatabase() {
        Stream.of(experimentRepository, configurationRepository, computationRepository)
                .map(ReactiveCrudRepository::deleteAll)
                .forEach(Mono::block);

        saveAllToDatabase();
    }

    private void saveAllToDatabase() {
        Stream.of(
                experimentRepository.saveAll(ALL_EXPERIMENTS),
                configurationRepository.saveAll(ALL_CONFIGURATIONS),
                computationRepository.saveAll(ALL_COMPUTATIONS)
        )
                .forEach(Flux::blockLast);
    }
}
