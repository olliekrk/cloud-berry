package com.cloudberry.cloudberry.db.mongo.service.experiment.delete;

import com.cloudberry.cloudberry.common.syntax.ListSyntax;
import com.cloudberry.cloudberry.db.mongo.data.metadata.Experiment;
import com.cloudberry.cloudberry.db.mongo.data.metadata.ExperimentComputation;
import com.cloudberry.cloudberry.db.mongo.data.metadata.ExperimentConfiguration;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.cloudberry.cloudberry.db.mongo.service.DataGenerator.ALL_COMPUTATIONS;
import static com.cloudberry.cloudberry.db.mongo.service.DataGenerator.ALL_CONFIGURATIONS;
import static com.cloudberry.cloudberry.db.mongo.service.DataGenerator.ALL_EXPERIMENTS;
import static com.cloudberry.cloudberry.db.mongo.service.DataGenerator.EXPERIMENT_ID_1;
import static com.cloudberry.cloudberry.db.mongo.service.DataGenerator.EXPERIMENT_ID_2;
import static com.cloudberry.cloudberry.db.mongo.service.DataGenerator.EXPERIMENT_ID_3_NO_CONFIGURATIONS;
import static com.cloudberry.cloudberry.db.mongo.service.DataGenerator.TEST_COMPUTATION_1;
import static com.cloudberry.cloudberry.db.mongo.service.DataGenerator.TEST_COMPUTATION_2;
import static com.cloudberry.cloudberry.db.mongo.service.DataGenerator.TEST_COMPUTATION_3;
import static com.cloudberry.cloudberry.db.mongo.service.DataGenerator.TEST_COMPUTATION_4;
import static com.cloudberry.cloudberry.db.mongo.service.DataGenerator.TEST_CONFIGURATION_1;
import static com.cloudberry.cloudberry.db.mongo.service.DataGenerator.TEST_CONFIGURATION_2;
import static com.cloudberry.cloudberry.db.mongo.service.DataGenerator.TEST_CONFIGURATION_3;
import static com.cloudberry.cloudberry.db.mongo.service.DataGenerator.TEST_CONFIGURATION_4_NO_COMPUTATIONS;
import static com.cloudberry.cloudberry.db.mongo.service.DataGenerator.TEST_EXPERIMENT_1;
import static com.cloudberry.cloudberry.db.mongo.service.DataGenerator.TEST_EXPERIMENT_2;
import static com.cloudberry.cloudberry.db.mongo.service.DataGenerator.TEST_EXPERIMENT_3_NO_CONFIGURATIONS;
import static org.hamcrest.MatcherAssert.assertThat;

public class ExperimentMetaDeletionServiceDeleteTest extends ExperimentMetaDeletionServiceTestBase {

    @Test
    void deleteHierarchicallyById() {
        saveAllToDatabase();

        experimentMetaDeletionService.deleteExperimentById(List.of(EXPERIMENT_ID_1)).blockLast();

        var expectedExperiments = ListSyntax.without(ALL_EXPERIMENTS, TEST_EXPERIMENT_1);
        var expectedConfigurations = List.of(TEST_CONFIGURATION_3, TEST_CONFIGURATION_4_NO_COMPUTATIONS);
        var expectedComputations = List.of(TEST_COMPUTATION_3, TEST_COMPUTATION_4);

        assertProperEntitiesInDb(expectedExperiments, expectedConfigurations, expectedComputations);
    }

    @Test
    void deleteWhenNoConfigurationForExperiment() {
        saveAllToDatabase();

        experimentMetaDeletionService.deleteExperimentById(List.of(EXPERIMENT_ID_3_NO_CONFIGURATIONS)).blockLast();

        var expectedExperiments = ListSyntax.without(ALL_EXPERIMENTS, TEST_EXPERIMENT_3_NO_CONFIGURATIONS);
        var expectedConfigurations = ALL_CONFIGURATIONS;
        var expectedComputations = ALL_COMPUTATIONS;

        assertProperEntitiesInDb(expectedExperiments, expectedConfigurations, expectedComputations);
    }

    @Test
    void deleteWhenNoComputationForConfiguration() {
        saveAllToDatabase();

        experimentMetaDeletionService.deleteExperimentById(List.of(EXPERIMENT_ID_2)).blockLast();

        var expectedExperiments = ListSyntax.without(ALL_EXPERIMENTS, TEST_EXPERIMENT_2);
        var expectedConfigurations = List.of(TEST_CONFIGURATION_1, TEST_CONFIGURATION_2);
        var expectedComputations = List.of(TEST_COMPUTATION_1, TEST_COMPUTATION_2);

        assertProperEntitiesInDb(expectedExperiments, expectedConfigurations, expectedComputations);
    }

    private void assertProperEntitiesInDb(List<Experiment> expectedExperiments,
                                          List<ExperimentConfiguration> expectedConfigurations,
                                          List<ExperimentComputation> expectedComputations) {
        var actualExperiments = experimentRepository.findAll().collectList().block();
        var actualConfigurations = configurationRepository.findAll().collectList().block();
        var actualComputations = computationRepository.findAll().collectList().block();

        assertThat(actualExperiments, Matchers.containsInAnyOrder(expectedExperiments.toArray()));
        assertThat(actualConfigurations, Matchers.containsInAnyOrder(expectedConfigurations.toArray()));
        assertThat(actualComputations, Matchers.containsInAnyOrder(expectedComputations.toArray()));
    }

}