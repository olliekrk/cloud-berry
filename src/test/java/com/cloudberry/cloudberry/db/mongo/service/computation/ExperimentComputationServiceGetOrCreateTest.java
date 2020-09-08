package com.cloudberry.cloudberry.db.mongo.service.computation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;

public class ExperimentComputationServiceGetOrCreateTest extends ExperimentComputationServiceTestBase {
    @Test
    void findExisting() {
        computationRepository.save(TEST_COMPUTATION_1A).block();

        getOrCreateAndCompare();
    }

    @Test
    void createNew() {
        var previousComputation = computationRepository.findById(TEST_COMPUTATION_1A.getId()).block();
        assertNull(previousComputation);

        getOrCreateAndCompare();
    }

    private void getOrCreateAndCompare() {
        var foundComputation = experimentComputationService.getOrCreateComputation(TEST_COMPUTATION_1A).block();
        var latterComputation = computationRepository.findById(TEST_COMPUTATION_1A.getId()).block();

        Assertions.assertEquals(TEST_COMPUTATION_1A, foundComputation);
        Assertions.assertEquals(TEST_COMPUTATION_1A, latterComputation);
    }
}
