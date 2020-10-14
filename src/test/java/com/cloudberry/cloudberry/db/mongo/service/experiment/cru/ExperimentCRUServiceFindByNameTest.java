package com.cloudberry.cloudberry.db.mongo.service.experiment.cru;

import com.cloudberry.cloudberry.db.mongo.data.metadata.Experiment;
import org.bson.types.ObjectId;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ExperimentCRUServiceFindByNameTest extends ExperimentCRUServiceTestBase {
    @Test
    void findByNameEmpty() {
        final var experiments = experimentCRUService.findByName(EXPERIMENT_NAME_1);

        Assertions.assertTrue(experiments.isEmpty());
    }

    @Test
    void findByNameSingle() {
        experimentRepository.saveAll(List.of(TEST_EXPERIMENT_1, TEST_EXPERIMENT_2)).blockLast();

        final var experiments = experimentCRUService.findByName(EXPERIMENT_NAME_1);

        assertThat(experiments, Matchers.containsInAnyOrder(TEST_EXPERIMENT_1));
    }

    @Test
    void findByNameMultiple() {
        final var additionalExperiment =
                new Experiment(ObjectId.get(), EXPERIMENT_NAME_1, Map.of(), Instant.ofEpochMilli(10));
        experimentRepository.saveAll(List.of(TEST_EXPERIMENT_1, TEST_EXPERIMENT_2, additionalExperiment)).blockLast();

        var experiments = experimentCRUService.findByName(EXPERIMENT_NAME_1);

        var expected = List.of(TEST_EXPERIMENT_1, additionalExperiment);
        assertThat(experiments, Matchers.containsInAnyOrder(expected.toArray()));
    }

}
