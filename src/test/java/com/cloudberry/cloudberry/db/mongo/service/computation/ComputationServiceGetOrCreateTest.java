package com.cloudberry.cloudberry.db.mongo.service.computation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;

public class ComputationServiceGetOrCreateTest extends ComputationServiceTestBase {
    @Test
    void findExisting() {
        computationsRepository.save(TEST_COMPUTATION_1A).block();

        getOrCreateAndCompare();
    }

    @Test
    void createNew() {
        var previousComputation = computationsRepository.findById(TEST_COMPUTATION_1A.getId()).block();
        assertNull(previousComputation);

        getOrCreateAndCompare();
    }

    private void getOrCreateAndCompare() {
        var foundComputation = computationService.getOrCreateComputation(TEST_COMPUTATION_1A).block();
        var latterComputation = computationsRepository.findById(TEST_COMPUTATION_1A.getId()).block();

        Assertions.assertEquals(TEST_COMPUTATION_1A, foundComputation);
        Assertions.assertEquals(TEST_COMPUTATION_1A, latterComputation);
    }
}
