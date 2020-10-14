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

import static com.cloudberry.cloudberry.db.mongo.service.DataGenerator.EXPERIMENT_ID_1;
import static com.cloudberry.cloudberry.db.mongo.service.DataGenerator.EXPERIMENT_ID_2;
import static com.cloudberry.cloudberry.db.mongo.service.DataGenerator.EXPERIMENT_ID_3_NO_CONFIGURATIONS;
import static com.cloudberry.cloudberry.db.mongo.service.DataGenerator.TEST_COMPUTATION_1_A_A;
import static com.cloudberry.cloudberry.db.mongo.service.DataGenerator.TEST_COMPUTATION_1_B_A;
import static com.cloudberry.cloudberry.db.mongo.service.DataGenerator.TEST_COMPUTATION_2_A_A;
import static com.cloudberry.cloudberry.db.mongo.service.DataGenerator.TEST_COMPUTATION_2_A_B;
import static org.hamcrest.MatcherAssert.assertThat;

@Import(ComputationsByDifferentIdsService.class)
public class FindByExperimentIdTest extends FindByIdsTestBase {

    @Autowired
    protected ComputationsByDifferentIdsService computationsByDifferentIdsService;

    private static Stream<Arguments> findByExperimentIdSource() {
        return Stream.of(
                Arguments.of(EXPERIMENT_ID_1, List.of(TEST_COMPUTATION_1_A_A, TEST_COMPUTATION_1_B_A)),
                Arguments.of(EXPERIMENT_ID_2, List.of(TEST_COMPUTATION_2_A_A, TEST_COMPUTATION_2_A_B)),
                Arguments.of(EXPERIMENT_ID_3_NO_CONFIGURATIONS, List.of())
        );
    }

    @ParameterizedTest
    @MethodSource("findByExperimentIdSource")
    void findByExperimentId(ObjectId experimentId, List<ExperimentComputation> expectedComputations) {
        var foundComputations = computationsByDifferentIdsService
                .findByExperimentId(experimentId).collectList().block();

        assertThat(foundComputations, Matchers.containsInAnyOrder(expectedComputations.toArray()));
    }
}
