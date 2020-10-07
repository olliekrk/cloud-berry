package com.cloudberry.cloudberry.db.mongo.service.computation;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExperimentComputationServiceFindAllTest extends ExperimentComputationServiceTestBase {
    @Test
    void findAllEmpty() {
        var computations = experimentComputationService.findAll();

        assertEquals(List.of(), computations);
    }

    @Test
    void findAll() {
        saveAllInRepository();

        var foundComputations = experimentComputationService.findAll();

        assertThat(foundComputations, Matchers.containsInAnyOrder(ALL_COMPUTATIONS.toArray()));
    }
}
