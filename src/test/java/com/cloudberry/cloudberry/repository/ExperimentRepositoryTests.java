package com.cloudberry.cloudberry.repository;

import com.cloudberry.cloudberry.EmbeddedMongoTests;
import com.cloudberry.cloudberry.model.metadata.Experiment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;

import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ComponentScan("com.cloudberry.cloudberry.util")
@EmbeddedMongoTests
public class ExperimentRepositoryTests {

    @Autowired
    ExperimentsRepository experimentsRepository;

    @Test
    @DisplayName("it should not change ID on experiment name modification")
    public void noIdChangeOnUpdateTest() {
        var experiment = experimentsRepository.save(newTestExperiment())
                .block();
        var modifiedExperiment = experimentsRepository.save(experiment.withName(experiment.getName() + "_modified"))
                .block();

        assertNotNull(experiment.getId());
        assertEquals(experiment.getId(), modifiedExperiment.getId());
    }

    @Test
    @DisplayName("it should generate subsequent IDs on insert")
    public void subsequentIdGenerationTest() {
        var experiment = experimentsRepository.save(newTestExperiment()).block();
        var experimentNext = experimentsRepository.save(newTestExperiment()).block();

        assertNotNull(experiment.getId());
        assertNotNull(experimentNext.getId());
        assertEquals(experiment.getId() + 1, experimentNext.getId());
    }

    private Experiment newTestExperiment() {
        return new Experiment(Instant.EPOCH, "TestExperiment", Map.of());
    }

}
