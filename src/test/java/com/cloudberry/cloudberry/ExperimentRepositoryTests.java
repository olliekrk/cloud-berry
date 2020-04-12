package com.cloudberry.cloudberry;

import com.cloudberry.cloudberry.model.metadata.Experiment;
import com.cloudberry.cloudberry.repository.ExperimentsRepository;
import com.cloudberry.cloudberry.util.listener.ExperimentIdListener;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@EmbeddedMongoTests
public class ExperimentRepositoryTests {

    @Autowired
    ExperimentsRepository experimentsRepository;
    @Autowired
    ExperimentIdListener experimentIdListener;

    @Test
    @DisplayName("it should not change ID on experiment name modification")
    public void noIdChangeOnUpdateTest() {
        var experiment = experimentsRepository.save(newTestExperiment()).block();
        var id = experiment.getId();
        experiment.setName(experiment.getName() + "_modified");
        experiment = experimentsRepository.save(experiment).block();
        assertEquals(id, experiment.getId());
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
        return new Experiment("TestExperiment", Map.of());
    }

}
