package com.cloudberry.cloudberry.db.mongo.service.experiment;

import com.cloudberry.cloudberry.EmbeddedMongoTest;
import com.cloudberry.cloudberry.db.mongo.data.metadata.Experiment;
import com.cloudberry.cloudberry.db.mongo.repository.ExperimentRepository;
import com.cloudberry.cloudberry.db.mongo.service.ExperimentService;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.util.Map;

import static java.time.Instant.ofEpochMilli;

@Import(ExperimentService.class)
@EmbeddedMongoTest
abstract class ExperimentServiceTestBase {
    protected static final String EXPERIMENT_NAME_1 = "experiment_test_1";
    protected static final String EXPERIMENT_NAME_2 = "experiment_test_2";
    protected static final Map<String, Object> EXPERIMENT_PARAMS_1 = Map.of("price", 3000);
    protected static final Map<String, Object> EXPERIMENT_PARAMS_2 = Map.of("param", 1);
    protected static final Experiment TEST_EXPERIMENT_1 =
            new Experiment(ObjectId.get(), EXPERIMENT_NAME_1, EXPERIMENT_PARAMS_1, ofEpochMilli(100));
    protected static final Experiment TEST_EXPERIMENT_2 =
            new Experiment(ObjectId.get(), EXPERIMENT_NAME_2, EXPERIMENT_PARAMS_2, ofEpochMilli(200));

    @Autowired
    protected ExperimentService experimentService;

    @Autowired
    protected ExperimentRepository experimentRepository;

    @BeforeEach
    void cleanDatabase() {
        experimentRepository.deleteAll().block();
    }

    protected void assertExperimentInDatabase(Experiment experiment) {
        final var foundExperiment = experimentRepository.findById(experiment.getId()).block();
        Assertions.assertEquals(experiment, foundExperiment);
    }
}
