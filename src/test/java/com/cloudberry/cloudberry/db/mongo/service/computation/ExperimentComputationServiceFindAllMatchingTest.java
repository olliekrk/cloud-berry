package com.cloudberry.cloudberry.db.mongo.service.computation;

import com.cloudberry.cloudberry.db.mongo.data.metadata.ExperimentComputation;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

public class ExperimentComputationServiceFindAllMatchingTest extends ExperimentComputationServiceTestBase {

    private static Stream<Arguments> provideParameters() {
        return Stream.of(
                Arguments.of(CONFIGURATION_ID_1, List.of(TEST_COMPUTATION_1A, TEST_COMPUTATION_1B)),
                Arguments.of(CONFIGURATION_ID_2, List.of(TEST_COMPUTATION_2)),
                Arguments.of(ObjectId.get(), List.of())
        );
    }

    @ParameterizedTest
    @MethodSource("provideParameters")
    void findMatching(ObjectId configurationId, List<ExperimentComputation> expectedComputations) {
        saveAllInRepository();

        var foundComputations = experimentComputationService.findAllComputationsForConfigurationId(configurationId);

        Assertions.assertEquals(expectedComputations, foundComputations);
    }
}