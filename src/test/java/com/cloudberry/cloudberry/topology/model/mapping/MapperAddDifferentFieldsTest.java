package com.cloudberry.cloudberry.topology.model.mapping;

import com.cloudberry.cloudberry.kafka.event.generic.ComputationEvent;
import com.cloudberry.cloudberry.topology.model.mapping.arguments.EntryMapArgument;
import com.cloudberry.cloudberry.topology.model.mapping.arguments.EntryMapRecord;
import com.cloudberry.cloudberry.topology.model.mapping.operators.OperationEnum;
import lombok.val;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static com.cloudberry.cloudberry.topology.model.ComputationEventMapType.FIELDS;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MapperAddDifferentFieldsTest {

    private static final String POPULATION_COMBINED_KEY = "populationCombined";
    private static final String POPULATION_LEFT_KEY = "populationLeft";
    private static final String POPULATION_RIGHT_KEY = "populationRight";

    private static final int POPULATION_LEFT_VALUE = 3000;
    private static final int POPULATION_RIGHT_VALUE = 5000;

    private static Stream<Arguments> createNewFieldSource() {
        return Stream.of(
                // both required to sum fields exist
                Arguments.of(
                        Map.of(
                                POPULATION_LEFT_KEY, POPULATION_LEFT_VALUE,
                                POPULATION_RIGHT_KEY, POPULATION_RIGHT_VALUE
                        ),
                        Map.of(POPULATION_LEFT_KEY, POPULATION_LEFT_VALUE,
                               POPULATION_RIGHT_KEY, POPULATION_RIGHT_VALUE,
                               POPULATION_COMBINED_KEY, (double) POPULATION_LEFT_VALUE + POPULATION_RIGHT_VALUE
                        )
                ),
                // only one of required to sum fields exist
                Arguments.of(
                        Map.of(POPULATION_LEFT_KEY, POPULATION_LEFT_VALUE),
                        Map.of(
                                POPULATION_LEFT_KEY, POPULATION_LEFT_VALUE,
                                POPULATION_COMBINED_KEY, (double) POPULATION_LEFT_VALUE
                        )
                ),
                // none of required to sum fields exist
                Arguments.of(
                        Map.of(),
                        Map.of(POPULATION_COMBINED_KEY, 0d)
                )
        );
    }

    @ParameterizedTest
    @MethodSource("createNewFieldSource")
    void createNewFieldFromSumOfOthers(Map<String, Object> incomingFields, Map<String, Object> expectedFields) {

        val incomingEvent = new ComputationEvent(
                Instant.ofEpochMilli(0),
                "some-test-measurement-name",
                incomingFields,
                Map.of()
        );

        val newEvent = Mapper.calculateNewComputationEvent(incomingEvent, new MappingExpression(
                List.of(new MappingEvaluation<>(
                                POPULATION_COMBINED_KEY,
                                FIELDS,
                                OperationEnum.ADD_DIFFERENT_FIELDS,
                                List.of(
                                        new EntryMapArgument(new EntryMapRecord(FIELDS, POPULATION_LEFT_KEY)),
                                        new EntryMapArgument(new EntryMapRecord(FIELDS, POPULATION_RIGHT_KEY))
                                )
                        )
                )));

        assertEquals(expectedFields, newEvent.getFields());
    }
}
