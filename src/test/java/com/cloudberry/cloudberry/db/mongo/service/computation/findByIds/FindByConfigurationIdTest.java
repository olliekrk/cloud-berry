package com.cloudberry.cloudberry.db.mongo.service.computation.findByIds;

import com.cloudberry.cloudberry.db.mongo.data.metadata.ExperimentComputation;
import com.cloudberry.cloudberry.db.mongo.service.FindByIdsTestBase;
import com.cloudberry.cloudberry.db.mongo.service.computation.ComputationsByDifferentIdsService;
import org.bson.types.ObjectId;
import org.hamcrest.Matchers;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.stream.Stream;

import static com.cloudberry.cloudberry.db.mongo.service.DataGenerator.CONFIGURATION_ID_1_A;
import static com.cloudberry.cloudberry.db.mongo.service.DataGenerator.CONFIGURATION_ID_1_B;
import static com.cloudberry.cloudberry.db.mongo.service.DataGenerator.CONFIGURATION_ID_2_A;
import static com.cloudberry.cloudberry.db.mongo.service.DataGenerator.CONFIGURATION_ID_2_B_NO_COMPUTATIONS;
import static com.cloudberry.cloudberry.db.mongo.service.DataGenerator.TEST_COMPUTATION_1_A_A;
import static com.cloudberry.cloudberry.db.mongo.service.DataGenerator.TEST_COMPUTATION_1_B_A;
import static com.cloudberry.cloudberry.db.mongo.service.DataGenerator.TEST_COMPUTATION_2_A_A;
import static com.cloudberry.cloudberry.db.mongo.service.DataGenerator.TEST_COMPUTATION_2_A_B;
import static org.hamcrest.MatcherAssert.assertThat;

@Import(ComputationsByDifferentIdsService.class)
public class FindByConfigurationIdTest extends FindByIdsTestBase {

    @Autowired
    protected ComputationsByDifferentIdsService computationsByDifferentIdsService;

    private static Stream<Arguments> findByConfigurationIdSource() {
        return Stream.of(
                Arguments.of(CONFIGURATION_ID_1_A, List.of(TEST_COMPUTATION_1_A_A)),
                Arguments.of(CONFIGURATION_ID_1_B, List.of(TEST_COMPUTATION_1_B_A)),
                Arguments.of(CONFIGURATION_ID_2_A, List.of(TEST_COMPUTATION_2_A_A, TEST_COMPUTATION_2_A_B)),
                Arguments.of(CONFIGURATION_ID_2_B_NO_COMPUTATIONS, List.of())
        );
    }

    @ParameterizedTest
    @MethodSource("findByConfigurationIdSource")
    void findByConfigurationId(ObjectId configurationId, List<ExperimentComputation> expectedComputations) {
        var foundComputations = computationsByDifferentIdsService
                .findByConfigurationId(configurationId).collectList().block();

        assertThat(foundComputations, Matchers.containsInAnyOrder(expectedComputations.toArray()));
    }
}
