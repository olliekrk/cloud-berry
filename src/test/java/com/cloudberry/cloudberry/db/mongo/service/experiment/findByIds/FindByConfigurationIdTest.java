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
public class FindByConfigurationIdTest extends FindByIdsTestBase {

    @Autowired
    ExperimentByDifferentIdsService experimentByDifferentIdsService;

    private static Stream<Arguments> findByConfigurationIdSource() {
        return Stream.of(
                Arguments.of(DataGenerator.CONFIGURATION_ID_1_A, TEST_EXPERIMENT_1),
                Arguments.of(DataGenerator.CONFIGURATION_ID_1_B, TEST_EXPERIMENT_1),
                Arguments.of(DataGenerator.CONFIGURATION_ID_2_A, TEST_EXPERIMENT_2),
                Arguments.of(DataGenerator.CONFIGURATION_ID_2_B_NO_COMPUTATIONS, TEST_EXPERIMENT_2)
        );
    }

    @ParameterizedTest
    @MethodSource("findByConfigurationIdSource")
    void findByConfigurationId(ObjectId configurationId, Experiment expectedExperiment) {
        var foundExperiment = experimentByDifferentIdsService.findByConfigurationId(configurationId).block();

        Assertions.assertEquals(expectedExperiment, foundExperiment);
    }
}
