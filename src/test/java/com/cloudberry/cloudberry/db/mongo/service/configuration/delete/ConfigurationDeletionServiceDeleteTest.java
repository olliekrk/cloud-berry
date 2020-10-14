package com.cloudberry.cloudberry.db.mongo.service.configuration.delete;

import com.cloudberry.cloudberry.common.syntax.ListSyntax;
import com.cloudberry.cloudberry.db.mongo.data.metadata.ExperimentComputation;
import com.cloudberry.cloudberry.db.mongo.data.metadata.ExperimentConfiguration;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.cloudberry.cloudberry.db.mongo.service.DataGenerator.ALL_COMPUTATIONS;
import static com.cloudberry.cloudberry.db.mongo.service.DataGenerator.ALL_CONFIGURATIONS;
import static com.cloudberry.cloudberry.db.mongo.service.DataGenerator.CONFIGURATION_ID_1_B;
import static com.cloudberry.cloudberry.db.mongo.service.DataGenerator.CONFIGURATION_ID_2_A;
import static com.cloudberry.cloudberry.db.mongo.service.DataGenerator.CONFIGURATION_ID_2_B_NO_COMPUTATIONS;
import static com.cloudberry.cloudberry.db.mongo.service.DataGenerator.TEST_COMPUTATION_1_A_A;
import static com.cloudberry.cloudberry.db.mongo.service.DataGenerator.TEST_CONFIGURATION_1_A;
import static com.cloudberry.cloudberry.db.mongo.service.DataGenerator.TEST_CONFIGURATION_2_B_NO_COMPUTATIONS;
import static org.hamcrest.MatcherAssert.assertThat;

public class ConfigurationDeletionServiceDeleteTest extends ConfigurationDeletionServiceTestBase {

    @Test
    void deleteHierarchicallyById() {
        saveAllToDatabase();

        configurationDeletionService.deleteConfigurationById(List.of(CONFIGURATION_ID_1_B, CONFIGURATION_ID_2_A))
                .blockLast();

        var expectedConfigurations = List.of(TEST_CONFIGURATION_1_A, TEST_CONFIGURATION_2_B_NO_COMPUTATIONS);
        var expectedComputations = List.of(TEST_COMPUTATION_1_A_A);

        assertProperEntitiesInDb(expectedConfigurations, expectedComputations);
    }

    @Test
    void deleteWhenNoComputationsFotThisConfiguration() {
        saveAllToDatabase();

        configurationDeletionService.deleteConfigurationById(List.of(CONFIGURATION_ID_2_B_NO_COMPUTATIONS))
                .blockLast();

        var expectedConfigurations = ListSyntax.without(ALL_CONFIGURATIONS, TEST_CONFIGURATION_2_B_NO_COMPUTATIONS);
        var expectedComputations = ALL_COMPUTATIONS;

        assertProperEntitiesInDb(expectedConfigurations, expectedComputations);
    }

    private void assertProperEntitiesInDb(List<ExperimentConfiguration> expectedConfigurations,
                                          List<ExperimentComputation> expectedComputations) {
        var actualConfigurations = configurationRepository.findAll().collectList().block();
        var actualComputations = computationRepository.findAll().collectList().block();

        assertThat(actualConfigurations, Matchers.containsInAnyOrder(expectedConfigurations.toArray()));
        assertThat(actualComputations, Matchers.containsInAnyOrder(expectedComputations.toArray()));
    }
}
