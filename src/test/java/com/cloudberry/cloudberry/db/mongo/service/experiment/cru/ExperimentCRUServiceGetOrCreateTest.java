package com.cloudberry.cloudberry.db.mongo.service.experiment.cru;

import com.cloudberry.cloudberry.db.mongo.data.metadata.Experiment;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ExperimentCRUServiceGetOrCreateTest extends ExperimentCRUServiceTestBase {
    @Test
    void findExisting() {
        experimentRepository.save(TEST_EXPERIMENT_1).block();

        getOrCreateAndCompare();
    }

    @Test
    void findExistingByNameAndParams() {
        experimentRepository.save(TEST_EXPERIMENT_1).block();
        var searchedExperiment =
                new Experiment(ObjectId.get(), TEST_EXPERIMENT_1.getName(), TEST_EXPERIMENT_1.getParameters(),
                               Instant.ofEpochSecond(10)
                );

        var foundExperiment = experimentCRUService.findOrCreateExperiment(searchedExperiment).block();

        assertEquals(TEST_EXPERIMENT_1, foundExperiment);
    }

    @Test
    void findByDifferentParamsCreatesNew() {
        experimentRepository.save(TEST_EXPERIMENT_1).block();
        var searchedExperiment = new Experiment(ObjectId.get(), TEST_EXPERIMENT_1.getName(), Map.of("fake param", 1),
                                                Instant.ofEpochSecond(10)
        );

        var foundExperiment = experimentCRUService.findOrCreateExperiment(searchedExperiment).block();
        var latterExperiment = experimentRepository.findById(searchedExperiment.getId()).block();

        assertNotEquals(TEST_EXPERIMENT_1, foundExperiment);
        assertEquals(foundExperiment, latterExperiment);
    }

    @Test
    void createNew() {
        var previousExperiment = experimentRepository.findById(TEST_EXPERIMENT_1.getId()).block();
        assertNull(previousExperiment);

        getOrCreateAndCompare();
    }

    private void getOrCreateAndCompare() {
        var foundExperiment = experimentCRUService.findOrCreateExperiment(TEST_EXPERIMENT_1).block();
        var latterExperiment = experimentRepository.findById(TEST_EXPERIMENT_1.getId()).block();

        assertEquals(TEST_EXPERIMENT_1, foundExperiment);
        assertEquals(TEST_EXPERIMENT_1, latterExperiment);
    }
}
