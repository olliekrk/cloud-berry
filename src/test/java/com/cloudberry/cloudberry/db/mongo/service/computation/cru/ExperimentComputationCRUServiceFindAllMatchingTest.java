package com.cloudberry.cloudberry.db.mongo.service.computation.cru;

import com.cloudberry.cloudberry.db.mongo.data.metadata.ExperimentComputation;
import org.bson.types.ObjectId;
import org.hamcrest.Matchers;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;

public class ExperimentComputationCRUServiceFindAllMatchingTest extends ExperimentComputationCRUServiceTestBase {

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

        var foundComputations = experimentComputationCRUService.findAllComputationsForConfigurationId(configurationId);

        assertThat(foundComputations, Matchers.containsInAnyOrder(expectedComputations.toArray()));
    }
}
