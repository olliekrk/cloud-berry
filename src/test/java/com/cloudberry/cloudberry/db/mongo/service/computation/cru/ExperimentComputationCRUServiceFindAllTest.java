package com.cloudberry.cloudberry.db.mongo.service.computation.cru;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExperimentComputationCRUServiceFindAllTest extends ExperimentComputationCRUServiceTestBase {
    @Test
    void findAllEmpty() {
        var computations = experimentComputationCRUService.findAll().collectList().block();

        assertEquals(List.of(), computations);
    }

    @Test
    void findAll() {
        saveAllInRepository();

        var foundComputations = experimentComputationCRUService.findAll().collectList().block();

        assertThat(foundComputations, Matchers.containsInAnyOrder(ALL_COMPUTATIONS.toArray()));
    }
}
