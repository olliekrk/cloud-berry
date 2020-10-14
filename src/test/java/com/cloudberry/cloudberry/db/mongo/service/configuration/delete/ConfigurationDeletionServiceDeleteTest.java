package com.cloudberry.cloudberry.db.mongo.service.configuration.delete;

import com.cloudberry.cloudberry.common.syntax.ListSyntax;
import com.cloudberry.cloudberry.db.mongo.data.metadata.ExperimentComputation;
import com.cloudberry.cloudberry.db.mongo.data.metadata.ExperimentConfiguration;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.cloudberry.cloudberry.db.mongo.service.DataGenerator.ALL_COMPUTATIONS;
import static com.cloudberry.cloudberry.db.mongo.service.DataGenerator.ALL_CONFIGURATIONS;
import static com.cloudberry.cloudberry.db.mongo.service.DataGenerator.CONFIGURATION_ID_2;
import static com.cloudberry.cloudberry.db.mongo.service.DataGenerator.CONFIGURATION_ID_3;
import static com.cloudberry.cloudberry.db.mongo.service.DataGenerator.CONFIGURATION_ID_4_NO_COMPUTATIONS;
import static com.cloudberry.cloudberry.db.mongo.service.DataGenerator.TEST_COMPUTATION_1;
import static com.cloudberry.cloudberry.db.mongo.service.DataGenerator.TEST_CONFIGURATION_1;
import static com.cloudberry.cloudberry.db.mongo.service.DataGenerator.TEST_CONFIGURATION_4_NO_COMPUTATIONS;
import static org.hamcrest.MatcherAssert.assertThat;

public class ConfigurationDeletionServiceDeleteTest extends ConfigurationDeletionServiceTestBase {

    @Test
    void deleteHierarchicallyById() {
        saveAllToDatabase();

        configurationDeletionService.deleteConfigurationById(List.of(CONFIGURATION_ID_2, CONFIGURATION_ID_3))
                .blockLast();

        var expectedConfigurations = List.of(TEST_CONFIGURATION_1, TEST_CONFIGURATION_4_NO_COMPUTATIONS);
        var expectedComputations = List.of(TEST_COMPUTATION_1);

        assertProperEntitiesInDb(expectedConfigurations, expectedComputations);
    }

    @Test
    void deleteWhenNoComputationsFotThisConfiguration() {
        saveAllToDatabase();

        configurationDeletionService.deleteConfigurationById(List.of(CONFIGURATION_ID_4_NO_COMPUTATIONS))
                .blockLast();

        var expectedConfigurations = ListSyntax.without(ALL_CONFIGURATIONS, TEST_CONFIGURATION_4_NO_COMPUTATIONS);
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
