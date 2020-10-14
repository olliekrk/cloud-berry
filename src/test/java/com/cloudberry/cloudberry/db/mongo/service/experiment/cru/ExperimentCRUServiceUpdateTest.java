package com.cloudberry.cloudberry.db.mongo.service.experiment.cru;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExperimentCRUServiceUpdateTest extends ExperimentCRUServiceTestBase {

    private static Stream<Arguments> provideParameters() {
        return Stream.of(
                Arguments.of(true, Map.of(), Map.of()),
                Arguments.of(true, Map.of("argument", 1), Map.of("argument", 1)),
                Arguments.of(true, Map.of("j", 21, "p", 37), Map.of("j", 21, "p", 37)),
                Arguments.of(false, Map.of("a", 1), Map.of("price", 3000, "a", 1)),
                Arguments.of(false, Map.of("price", 1), Map.of("price", 1)),
                Arguments.of(false, Map.of(), Map.of("price", 3000))
        );
    }

    @BeforeEach
    void saveTestExperiment() {
        super.cleanDatabase();
        experimentRepository.save(TEST_EXPERIMENT_1).block();
    }

    @Test
    void updateName() {
        final String newName = "new name";

        var updatedExperiment = experimentCRUService.update(TEST_EXPERIMENT_1.getId(), newName, null, false);

        assertEquals(TEST_EXPERIMENT_1.withName(newName), updatedExperiment);
        assertExperimentInDatabase(updatedExperiment);
    }

    @ParameterizedTest
    @MethodSource("provideParameters")
    void updateParams(boolean overrideParams, Map<String, Object> newParameters,
                      Map<String, Object> expectedParameters) {
        var updatedExperiment =
                experimentCRUService.update(TEST_EXPERIMENT_1.getId(), null, newParameters, overrideParams);

        assertEquals(expectedParameters, updatedExperiment.getParameters());
        assertExperimentInDatabase(updatedExperiment);
    }
}
