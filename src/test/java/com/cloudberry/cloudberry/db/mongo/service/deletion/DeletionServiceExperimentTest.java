package com.cloudberry.cloudberry.db.mongo.service.deletion;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;

public class DeletionServiceExperimentTest extends DeletionServiceTestBase {

    @Test
    void removalOfExperimentWith() {
        saveAllToDatabase();

        experimentDeletionService.deleteExperimentById(EXPERIMENT_ID_1).blockLast();

        var expectedExperiments = List.of(TEST_EXPERIMENT_2);
        var expectedConfigurations = List.of(TEST_CONFIGURATION_3);
        var expectedComputations = List.of(TEST_COMPUTATION_3);

        var actualExperiments = experimentRepository.findAll().collectList().block();
        var actualConfigurations = configurationRepository.findAll().collectList().block();
        var actualComputations = computationRepository.findAll().collectList().block();

        assertThat(actualExperiments, Matchers.containsInAnyOrder(expectedExperiments.toArray()));
        assertThat(actualConfigurations, Matchers.containsInAnyOrder(expectedConfigurations.toArray()));
        assertThat(actualComputations, Matchers.containsInAnyOrder(expectedComputations.toArray()));
    }
}
