package com.cloudberry.cloudberry.db.mongo.service.experiment;

import com.cloudberry.cloudberry.db.mongo.data.metadata.Experiment;
import org.bson.types.ObjectId;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ExperimentServiceFindByNameTest extends ExperimentServiceTestBase {
    @Test
    void findByNameEmpty() {
        var experiments = experimentService.findByName(EXPERIMENT_NAME_1);

        assertTrue(experiments.isEmpty());
    }

    @Test
    void findByNameSingle() {
        experimentRepository.saveAll(List.of(TEST_EXPERIMENT_1, TEST_EXPERIMENT_2)).blockLast();

        final var experiments = experimentService.findByName(EXPERIMENT_NAME_1);

        assertThat(experiments, Matchers.containsInAnyOrder(TEST_EXPERIMENT_1));
    }

    @Test
    void findByNameMultiple() {
        final var additionalExperiment =
                new Experiment(ObjectId.get(), EXPERIMENT_NAME_1, Map.of(), Instant.ofEpochMilli(10));
        experimentRepository.saveAll(List.of(TEST_EXPERIMENT_1, TEST_EXPERIMENT_2, additionalExperiment)).blockLast();

        var experiments = experimentService.findByName(EXPERIMENT_NAME_1);

        assertThat(experiments, Matchers.containsInAnyOrder(TEST_EXPERIMENT_1));
    }


}
