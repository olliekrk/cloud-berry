package com.cloudberry.cloudberry.db.mongo.service.experiment;

import com.cloudberry.cloudberry.db.mongo.data.metadata.Experiment;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;


class ExperimentServiceFindAllTest extends ExperimentServiceTestBase {
    @Test
    void findAllEmpty() {
        var experiments = experimentService.findAll();

        assertEquals(List.of(), experiments);
    }

    @Test
    void findAll() {
        final var experiments = List.of(TEST_EXPERIMENT_1, TEST_EXPERIMENT_2);
        experimentsRepository.saveAll(experiments).blockLast();

        var foundExperiments = experimentService.findAll();

        assertEquals(experiments, foundExperiments);
    }

    @Test
    void findAllAfterRemove() {
        final var experiments = List.of(TEST_EXPERIMENT_1, TEST_EXPERIMENT_2);
        experimentsRepository.saveAll(experiments).blockLast();
        experimentsRepository.saveAll(experiments).blockLast();

        experimentsRepository.delete(TEST_EXPERIMENT_2).block();
        var foundExperiments = experimentService.findAll();

        assertEquals(List.of(TEST_EXPERIMENT_1), foundExperiments);
    }
}