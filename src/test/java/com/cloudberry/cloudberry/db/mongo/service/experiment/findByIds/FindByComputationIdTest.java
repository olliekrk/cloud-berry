package com.cloudberry.cloudberry.db.mongo.service.experiment.findByIds;

import com.cloudberry.cloudberry.db.mongo.data.metadata.Experiment;
import com.cloudberry.cloudberry.db.mongo.service.DataGenerator;
import com.cloudberry.cloudberry.db.mongo.service.FindByIdsTestBase;
import com.cloudberry.cloudberry.db.mongo.service.configuration.ExperimentConfigurationByDifferentIdsService;
import com.cloudberry.cloudberry.db.mongo.service.experiment.ExperimentByDifferentIdsService;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.util.stream.Stream;

import static com.cloudberry.cloudberry.db.mongo.service.DataGenerator.TEST_EXPERIMENT_1;
import static com.cloudberry.cloudberry.db.mongo.service.DataGenerator.TEST_EXPERIMENT_2;

@Import({ExperimentByDifferentIdsService.class, ExperimentConfigurationByDifferentIdsService.class})
public class FindByComputationIdTest extends FindByIdsTestBase {

    @Autowired
    ExperimentByDifferentIdsService experimentByDifferentIdsService;

    private static Stream<Arguments> findByComputationIdSource() {
        return Stream.of(
                Arguments.of(DataGenerator.COMPUTATION_ID_1_A_A, TEST_EXPERIMENT_1),
                Arguments.of(DataGenerator.COMPUTATION_ID_1_B_A, TEST_EXPERIMENT_1),
                Arguments.of(DataGenerator.COMPUTATION_ID_2_A_A, TEST_EXPERIMENT_2),
                Arguments.of(DataGenerator.COMPUTATION_ID_2_A_B, TEST_EXPERIMENT_2)
        );
    }

    @ParameterizedTest
    @MethodSource("findByComputationIdSource")
    void findByComputationId(ObjectId computationId, Experiment expectedExperiment) {
        var foundExperiment = experimentByDifferentIdsService.findByComputationId(computationId).block();

        Assertions.assertEquals(expectedExperiment, foundExperiment);
    }
}
