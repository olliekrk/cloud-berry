package com.cloudberry.cloudberry.topology.model.mapping.operations.differentFieldsOperations;

import com.cloudberry.cloudberry.kafka.event.generic.ComputationEvent;
import com.cloudberry.cloudberry.topology.model.mapping.arguments.EntryMapArgument;
import com.cloudberry.cloudberry.topology.model.mapping.arguments.EntryMapRecord;
import com.cloudberry.cloudberry.topology.model.mapping.operations.ComputationMetaParameterExtractor;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static com.cloudberry.cloudberry.topology.model.ComputationEventMapType.FIELDS;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MultiplyDifferentFieldsTest {

    @Mock
    ComputationMetaParameterExtractor computationMetaParameterExtractor;

    private final MultiplyDifferentFields multiplyDifferentFields =
            new MultiplyDifferentFields(new EventExtractorUtils(computationMetaParameterExtractor));


    @Test
    void properMultiplyLotOfValues() {
        val velocityKey = "velocity";
        val timeKey = "time";
        val repetitionsKey = "repetitions";
        val velocity = 50.37;
        val time = 12;
        val repetitions = 5;

        val arguments = List.of(
                new EntryMapArgument(new EntryMapRecord(FIELDS, velocityKey)),
                new EntryMapArgument(new EntryMapRecord(FIELDS, timeKey)),
                new EntryMapArgument(new EntryMapRecord(FIELDS, repetitionsKey))
        );
        val event = new ComputationEvent(
                Instant.ofEpochMilli(0),
                "some-test-measurement-name",
                Map.of(velocityKey, velocity, timeKey, time, repetitionsKey, repetitions),
                Map.of()
        );

        val calculatedDistance = multiplyDifferentFields.calculateNewValue(arguments, event);

        val expectedDistance = velocity * time * repetitions;
        assertEquals(expectedDistance, calculatedDistance);
    }

    @Test
    void properMultiplyOneValue() {
        val velocityKey = "velocity";
        val timeKey = "time";
        val repetitionsKey = "repetitions";
        val velocity = 50.37;
        val time = 12;
        val repetitions = 5;

        val arguments = List.of(
                new EntryMapArgument(new EntryMapRecord(FIELDS, velocityKey))
        );
        val event = new ComputationEvent(
                Instant.ofEpochMilli(0),
                "some-test-measurement-name",
                Map.of(velocityKey, velocity, timeKey, time, repetitionsKey, repetitions),
                Map.of()
        );

        val calculatedDistance = multiplyDifferentFields.calculateNewValue(arguments, event);

        assertEquals(velocity, calculatedDistance);
    }
}
