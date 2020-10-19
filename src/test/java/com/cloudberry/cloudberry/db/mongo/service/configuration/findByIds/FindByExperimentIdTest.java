package com.cloudberry.cloudberry.db.mongo.service.configuration.findByIds;

import com.cloudberry.cloudberry.db.mongo.data.metadata.ExperimentConfiguration;
import com.cloudberry.cloudberry.db.mongo.service.FindByIdsTestBase;
import com.cloudberry.cloudberry.db.mongo.service.configuration.ExperimentConfigurationByDifferentIdsService;
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
import static com.cloudberry.cloudberry.db.mongo.service.DataGenerator.TEST_CONFIGURATION_1_A;
import static com.cloudberry.cloudberry.db.mongo.service.DataGenerator.TEST_CONFIGURATION_1_B;
import static com.cloudberry.cloudberry.db.mongo.service.DataGenerator.TEST_CONFIGURATION_2_A;
import static com.cloudberry.cloudberry.db.mongo.service.DataGenerator.TEST_CONFIGURATION_2_B_NO_COMPUTATIONS;
import static org.hamcrest.MatcherAssert.assertThat;

@Import({ExperimentConfigurationByDifferentIdsService.class})
public class FindByExperimentIdTest extends FindByIdsTestBase {

    @Autowired
    ExperimentConfigurationByDifferentIdsService experimentConfigurationByDifferentIdsService;

    private static Stream<Arguments> findByExperimentIdSource() {
        return Stream.of(
                Arguments.of(EXPERIMENT_ID_1, List.of(TEST_CONFIGURATION_1_A, TEST_CONFIGURATION_1_B)),
                Arguments.of(EXPERIMENT_ID_2, List.of(TEST_CONFIGURATION_2_A, TEST_CONFIGURATION_2_B_NO_COMPUTATIONS)),
                Arguments.of(EXPERIMENT_ID_3_NO_CONFIGURATIONS, List.of())
        );
    }

    @ParameterizedTest
    @MethodSource("findByExperimentIdSource")
    void findByExperimentId(ObjectId experimentId, List<ExperimentConfiguration> expectedConfigurations) {
        var foundConfigurations = experimentConfigurationByDifferentIdsService.findByExperimentId(experimentId)
                .collectList().block();

        assertThat(foundConfigurations, Matchers.containsInAnyOrder(expectedConfigurations.toArray()));
    }
}
