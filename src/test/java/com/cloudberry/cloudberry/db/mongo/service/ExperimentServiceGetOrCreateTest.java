package com.cloudberry.cloudberry.db.mongo.service;

import com.cloudberry.cloudberry.db.mongo.data.metadata.Experiment;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ExperimentServiceGetOrCreateTest extends ExperimentServiceTestBase {
    @Test
    void findExisting() {
        experimentsRepository.save(TEST_EXPERIMENT_1).block();

        var foundExperiment = experimentService.getOrCreateExperiment(TEST_EXPERIMENT_1).block();
        var latterExperiment = experimentsRepository.findById(TEST_EXPERIMENT_1.getId()).block();

        assertEquals(TEST_EXPERIMENT_1, foundExperiment);
        assertEquals(TEST_EXPERIMENT_1, latterExperiment);
    }

    @Test
    void findExistingByNameAndParams() {
        experimentsRepository.save(TEST_EXPERIMENT_1).block();
        var searchedExperiment = new Experiment(ObjectId.get(), TEST_EXPERIMENT_1.getName(), TEST_EXPERIMENT_1.getParameters(), Instant.ofEpochSecond(10));

        var foundExperiment = experimentService.getOrCreateExperiment(searchedExperiment).block();

        assertEquals(TEST_EXPERIMENT_1, foundExperiment);
    }

    @Test
    void findByDifferentParamsCreatesNew() {
        experimentsRepository.save(TEST_EXPERIMENT_1).block();
        var searchedExperiment = new Experiment(ObjectId.get(), TEST_EXPERIMENT_1.getName(), Map.of("fake param", 1), Instant.ofEpochSecond(10));

        var foundExperiment = experimentService.getOrCreateExperiment(searchedExperiment).block();
        var latterExperiment = experimentsRepository.findById(searchedExperiment.getId()).block();

        assertNotEquals(TEST_EXPERIMENT_1, foundExperiment);
        assertEquals(foundExperiment, latterExperiment);
    }

    @Test
    void createNew() {
        var previousExperiment = experimentsRepository.findById(TEST_EXPERIMENT_1.getId()).block();
        assertNull(previousExperiment);

        var foundExperiment = experimentService.getOrCreateExperiment(TEST_EXPERIMENT_1).block();
        var latterExperiment = experimentsRepository.findById(TEST_EXPERIMENT_1.getId()).block();

        assertEquals(TEST_EXPERIMENT_1, foundExperiment);
        assertEquals(TEST_EXPERIMENT_1, latterExperiment);
    }
}