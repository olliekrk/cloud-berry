package com.cloudberry.cloudberry.db.mongo.service.configuration.findByIds;

import com.cloudberry.cloudberry.db.mongo.data.metadata.ExperimentConfiguration;
import com.cloudberry.cloudberry.db.mongo.service.DataGenerator;
import com.cloudberry.cloudberry.db.mongo.service.FindByIdsTestBase;
import com.cloudberry.cloudberry.db.mongo.service.configuration.ExperimentConfigurationByDifferentIdsService;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.util.stream.Stream;

import static com.cloudberry.cloudberry.db.mongo.service.DataGenerator.TEST_CONFIGURATION_1_A;
import static com.cloudberry.cloudberry.db.mongo.service.DataGenerator.TEST_CONFIGURATION_1_B;
import static com.cloudberry.cloudberry.db.mongo.service.DataGenerator.TEST_CONFIGURATION_2_A;

@Import({ExperimentConfigurationByDifferentIdsService.class})
public class FindByComputationIdTest extends FindByIdsTestBase {

    @Autowired
    ExperimentConfigurationByDifferentIdsService experimentConfigurationByDifferentIdsService;

    private static Stream<Arguments> findByComputationIdSource() {
        return Stream.of(
                Arguments.of(DataGenerator.COMPUTATION_ID_1_A_A, TEST_CONFIGURATION_1_A),
                Arguments.of(DataGenerator.COMPUTATION_ID_1_B_A, TEST_CONFIGURATION_1_B),
                Arguments.of(DataGenerator.COMPUTATION_ID_2_A_A, TEST_CONFIGURATION_2_A),
                Arguments.of(DataGenerator.COMPUTATION_ID_2_A_B, TEST_CONFIGURATION_2_A)
        );
    }

    @ParameterizedTest
    @MethodSource("findByComputationIdSource")
    void findByComputationId(ObjectId computationId, ExperimentConfiguration expectedExperiment) {
        var foundExperiment = experimentConfigurationByDifferentIdsService.findByComputationId(computationId).block();

        Assertions.assertEquals(expectedExperiment, foundExperiment);
    }
}
