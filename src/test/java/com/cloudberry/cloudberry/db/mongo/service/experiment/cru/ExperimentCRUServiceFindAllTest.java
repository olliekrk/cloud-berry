package com.cloudberry.cloudberry.db.mongo.service.experiment.cru;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;


class ExperimentCRUServiceFindAllTest extends ExperimentCRUServiceTestBase {
    @Test
    void findAllEmpty() {
        var experiments = experimentCRUService.findAll();

        assertEquals(List.of(), experiments);
    }

    @Test
    void findAll() {
        final var experiments = List.of(TEST_EXPERIMENT_1, TEST_EXPERIMENT_2);
        experimentRepository.saveAll(experiments).blockLast();

        var foundExperiments = experimentCRUService.findAll();

        assertThat(foundExperiments, Matchers.containsInAnyOrder(experiments.toArray()));
    }

    @Test
    void findAllAfterRemove() {
        final var experiments = List.of(TEST_EXPERIMENT_1, TEST_EXPERIMENT_2);
        experimentRepository.saveAll(experiments).blockLast();
        experimentRepository.saveAll(experiments).blockLast();

        experimentRepository.delete(TEST_EXPERIMENT_2).block();
        var foundExperiments = experimentCRUService.findAll();

        assertThat(foundExperiments, Matchers.containsInAnyOrder(TEST_EXPERIMENT_1));
    }
}