package com.cloudberry.cloudberry.db.mongo.repository;

import com.cloudberry.cloudberry.EmbeddedMongoTest;
import com.cloudberry.cloudberry.db.mongo.data.metadata.Experiment;
import com.cloudberry.cloudberry.db.mongo.util.IdSequenceGenerator;
import com.cloudberry.cloudberry.db.mongo.util.listener.ExperimentIdListener;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Import({ExperimentIdListener.class, IdSequenceGenerator.class})
@EmbeddedMongoTest
public class ExperimentRepositoryTests {

    @Autowired
    ExperimentRepository experimentRepository;

    @Test
    @DisplayName("it should not change ID on experiment name modification")
    public void noIdChangeOnUpdateTest() {
        var experiment = experimentRepository.save(newTestExperiment())
                .block();
        var modifiedExperiment = experimentRepository.save(experiment.withName(experiment.getName() + "_modified"))
                .block();

        assertNotNull(experiment.getId());
        assertEquals(experiment.getId(), modifiedExperiment.getId());
    }

    @Test
    @DisplayName("it should generate subsequent IDs on insert")
    public void subsequentIdGenerationTest() {
        var experiment = experimentRepository.save(newTestExperiment()).block();
        var experimentNext = experimentRepository.save(newTestExperiment()).block();

        assertNotNull(experiment.getId());
        assertNotNull(experimentNext.getId());
//        assertEquals(experiment.getId() + 1, experimentNext.getId());
    }

    private Experiment newTestExperiment() {
        return new Experiment(Instant.EPOCH, "TestExperiment", Map.of());
    }

}
