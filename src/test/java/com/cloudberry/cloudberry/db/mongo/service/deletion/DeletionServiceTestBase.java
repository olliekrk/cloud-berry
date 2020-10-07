package com.cloudberry.cloudberry.db.mongo.service.deletion;

import com.cloudberry.cloudberry.EmbeddedMongoTest;
import com.cloudberry.cloudberry.db.mongo.data.metadata.Experiment;
import com.cloudberry.cloudberry.db.mongo.data.metadata.ExperimentComputation;
import com.cloudberry.cloudberry.db.mongo.data.metadata.ExperimentConfiguration;
import com.cloudberry.cloudberry.db.mongo.repository.ComputationRepository;
import com.cloudberry.cloudberry.db.mongo.repository.ConfigurationRepository;
import com.cloudberry.cloudberry.db.mongo.repository.ExperimentRepository;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.time.Instant.ofEpochMilli;
import static java.time.Instant.ofEpochSecond;

@Import({ExperimentDeletionService.class, ConfigurationDeletionService.class, ComputationDeletionService.class})
@EmbeddedMongoTest
abstract class DeletionServiceTestBase {

    //config experiment
    protected static final ObjectId EXPERIMENT_ID_1 = ObjectId.get();
    protected static final ObjectId EXPERIMENT_ID_2 = ObjectId.get();
    protected static final String EXPERIMENT_NAME_1 = "experiment_test_1";
    protected static final String EXPERIMENT_NAME_2 = "experiment_test_2";
    protected static final Map<String, Object> EXPERIMENT_PARAMS_1 = Map.of("price", 3000);
    protected static final Map<String, Object> EXPERIMENT_PARAMS_2 = Map.of("param", 1);
    protected static final Experiment TEST_EXPERIMENT_1 =
            new Experiment(EXPERIMENT_ID_1, EXPERIMENT_NAME_1, EXPERIMENT_PARAMS_1, ofEpochMilli(100));
    protected static final Experiment TEST_EXPERIMENT_2 =
            new Experiment(EXPERIMENT_ID_2, EXPERIMENT_NAME_2, EXPERIMENT_PARAMS_2, ofEpochMilli(200));

    protected static final List<Experiment> ALL_EXPERIMENTS = List.of(TEST_EXPERIMENT_1, TEST_EXPERIMENT_2);

    //config experiment configuration
    protected static final ObjectId CONFIGURATION_ID_1 = ObjectId.get();
    protected static final ObjectId CONFIGURATION_ID_2 = ObjectId.get();
    protected static final ObjectId CONFIGURATION_ID_3 = ObjectId.get();
    protected static final String CONFIGURATION_FILE_NAME_1 = "config_1";
    protected static final String CONFIGURATION_FILE_NAME_2 = "config_2";
    protected static final String CONFIGURATION_FILE_NAME_3 = "config_3";
    protected static final Map<String, Object> CONFIGURATION_PARAMS_1 = Map.of("price", 3000);
    protected static final Map<String, Object> CONFIGURATION_PARAMS_2 = Map.of();
    protected static final Map<String, Object> CONFIGURATION_PARAMS_3 = Map.of("owner", "Andrew");
    protected static final ExperimentConfiguration TEST_CONFIGURATION_1 =
            new ExperimentConfiguration(CONFIGURATION_ID_1, EXPERIMENT_ID_1, CONFIGURATION_FILE_NAME_1,
                    CONFIGURATION_PARAMS_1, ofEpochSecond(10));
    protected static final ExperimentConfiguration TEST_CONFIGURATION_2 =
            new ExperimentConfiguration(CONFIGURATION_ID_2, EXPERIMENT_ID_1, CONFIGURATION_FILE_NAME_2,
                    CONFIGURATION_PARAMS_2, ofEpochSecond(20));
    protected static final ExperimentConfiguration TEST_CONFIGURATION_3 =
            new ExperimentConfiguration(CONFIGURATION_ID_3, EXPERIMENT_ID_2, CONFIGURATION_FILE_NAME_3,
                    CONFIGURATION_PARAMS_3, ofEpochSecond(30));
    protected static final List<ExperimentConfiguration> ALL_CONFIGURATIONS =
            List.of(TEST_CONFIGURATION_1, TEST_CONFIGURATION_2, TEST_CONFIGURATION_3);

    //config experiment computation
    protected static final ObjectId COMPUTATION_ID_1 = ObjectId.get();
    protected static final ObjectId COMPUTATION_ID_2 = ObjectId.get();
    protected static final ObjectId COMPUTATION_ID_3 = ObjectId.get();
    protected static final ExperimentComputation TEST_COMPUTATION_1 =
            new ExperimentComputation(COMPUTATION_ID_1, CONFIGURATION_ID_1, Instant.ofEpochSecond(1));
    protected static final ExperimentComputation TEST_COMPUTATION_2 =
            new ExperimentComputation(COMPUTATION_ID_2, CONFIGURATION_ID_2, Instant.ofEpochSecond(10));
    protected static final ExperimentComputation TEST_COMPUTATION_3 =
            new ExperimentComputation(COMPUTATION_ID_3, CONFIGURATION_ID_3, Instant.ofEpochSecond(100));
    protected static final List<ExperimentComputation> ALL_COMPUTATIONS =
            List.of(TEST_COMPUTATION_1, TEST_COMPUTATION_2, TEST_COMPUTATION_3);


    @Autowired
    protected ExperimentDeletionService experimentDeletionService;

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
                experimentRepository.saveAll(ALL_EXPERIMENTS),
                configurationRepository.saveAll(ALL_CONFIGURATIONS),
                computationRepository.saveAll(ALL_COMPUTATIONS)
        )
                .forEach(Flux::blockLast);
    }

}