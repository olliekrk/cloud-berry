package com.cloudberry.cloudberry.db.mongo.service.computation.delete;

import com.cloudberry.cloudberry.common.syntax.ListSyntax;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.IntStream;

import static org.hamcrest.MatcherAssert.assertThat;

public class ComputationDeletionServiceDeleteTest extends ExperimentComputationDeletionServiceTestBase {
    @Test
    void deleteByIdPresent() {
        saveAllInRepository();

        computationDeletionService.deleteComputationById(TEST_COMPUTATION_1A.getId()).blockLast();

        var actual = computationRepository.findAll().collectList().block();
        var expected = ListSyntax.without(ALL_COMPUTATIONS, TEST_COMPUTATION_1A);
        assertThat(actual, Matchers.containsInAnyOrder(expected.toArray()));
    }

    @Test
    void deleteByIdPresentMultipleTimes() {
        saveAllInRepository();

        IntStream.of(3).forEach(
                i -> computationDeletionService.deleteComputationById(TEST_COMPUTATION_1A.getId()).blockLast());

        var actual = computationRepository.findAll().collectList().block();
        var expected = ListSyntax.without(ALL_COMPUTATIONS, TEST_COMPUTATION_1A);
        assertThat(actual, Matchers.containsInAnyOrder(expected.toArray()));
    }


    @Test
    void deleteByIdAbsent() {
        computationRepository.save(TEST_COMPUTATION_2).block();

        Assertions.assertDoesNotThrow(
                () -> computationDeletionService.deleteComputationById(TEST_COMPUTATION_1A.getId()).blockLast());

        var actual = computationRepository.findAll().collectList().block();
        var expected = List.of(TEST_COMPUTATION_2);
        assertThat(actual, Matchers.containsInAnyOrder(expected.toArray()));
    }
}
