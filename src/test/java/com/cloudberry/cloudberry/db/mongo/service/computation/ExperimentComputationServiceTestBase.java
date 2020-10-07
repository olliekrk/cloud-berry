package com.cloudberry.cloudberry.db.mongo.service.computation;

import com.cloudberry.cloudberry.EmbeddedMongoTest;
import com.cloudberry.cloudberry.db.mongo.data.metadata.ExperimentComputation;
import com.cloudberry.cloudberry.db.mongo.repository.ComputationRepository;
import com.cloudberry.cloudberry.db.mongo.service.ExperimentComputationService;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.time.Instant;
import java.util.List;

@Import(ExperimentComputationService.class)
@EmbeddedMongoTest
class ExperimentComputationServiceTestBase {
    protected static final ObjectId CONFIGURATION_ID_1 = ObjectId.get();
    protected static final ObjectId CONFIGURATION_ID_2 = ObjectId.get();
    protected static final ExperimentComputation TEST_COMPUTATION_1A =
            new ExperimentComputation(ObjectId.get(), CONFIGURATION_ID_1, Instant.ofEpochSecond(1));
    protected static final ExperimentComputation TEST_COMPUTATION_1B =
            new ExperimentComputation(CONFIGURATION_ID_1, Instant.ofEpochSecond(10));
    protected static final ExperimentComputation TEST_COMPUTATION_2 =
            new ExperimentComputation(CONFIGURATION_ID_2, Instant.ofEpochSecond(100));
    protected static final List<ExperimentComputation> ALL_COMPUTATIONS =
            List.of(TEST_COMPUTATION_1A, TEST_COMPUTATION_1B, TEST_COMPUTATION_2);

    @Autowired
    protected ExperimentComputationService experimentComputationService;

    @Autowired
    protected ComputationRepository computationRepository;

    @BeforeEach
    void cleanDatabase() {
        computationRepository.deleteAll().block();
    }

    protected void saveAllInRepository() {
        computationRepository.saveAll(ALL_COMPUTATIONS).blockLast();
    }

    protected void assertComputationInDatabase(ExperimentComputation experimentComputation) {

    }
}