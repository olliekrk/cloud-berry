package com.cloudberry.cloudberry.db.mongo.service.computation;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ComputationServiceFindAllTest extends ComputationServiceTestBase {
    @Test
    void findAllEmpty() {
        var computations = computationService.findAll();

        assertEquals(List.of(), computations);
    }

    @Test
    void findAll() {
        saveAllInRepository();

        var foundComputations = computationService.findAll();

        assertEquals(ALL_COMPUTATIONS, foundComputations);
    }
}
