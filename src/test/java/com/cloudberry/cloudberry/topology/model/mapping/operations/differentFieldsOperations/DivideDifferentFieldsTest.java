package com.cloudberry.cloudberry.topology.model.mapping.operations.differentFieldsOperations;

import com.cloudberry.cloudberry.kafka.event.generic.ComputationEvent;
import com.cloudberry.cloudberry.topology.model.mapping.arguments.EntryMapArgument;
import com.cloudberry.cloudberry.topology.model.mapping.arguments.EntryMapRecord;
import com.cloudberry.cloudberry.topology.model.mapping.operations.DivisionByZeroException;
import lombok.val;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static com.cloudberry.cloudberry.topology.model.ComputationEventMapType.FIELDS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


class DivideDifferentFieldsTest {

    @Test
    void calculateNewValueWhenFieldsExist() {
        final String distanceKey = "distance";
        final String timeKey = "time";
        final int speed = 300;
        final int time = 100;

        val arguments = List.of(
                new EntryMapArgument(new EntryMapRecord(FIELDS, distanceKey)),
                new EntryMapArgument(new EntryMapRecord(FIELDS, timeKey))
        );
        val event = new ComputationEvent(
                Instant.ofEpochMilli(0),
                "some-test-measurement-name",
                Map.of(distanceKey, speed, timeKey, time),
                Map.of()
        );

        val calculatedSpeed = DivideDifferentFields.calculateNewValue(arguments, event);

        val expectedSpeed = (double) speed / time;
        assertEquals(expectedSpeed, calculatedSpeed);
    }

    @Test
    void cannotDivideByZero() {
        final String distanceKey = "distance";
        final String timeKey = "time";
        final int speed = 300;
        final int time = 0;

        val arguments = List.of(
                new EntryMapArgument(new EntryMapRecord(FIELDS, distanceKey)),
                new EntryMapArgument(new EntryMapRecord(FIELDS, timeKey))
        );
        val event = new ComputationEvent(
                Instant.ofEpochMilli(0),
                "some-test-measurement-name",
                Map.of(distanceKey, speed, timeKey, time),
                Map.of()
        );

        assertThrows(DivisionByZeroException.class, () -> DivideDifferentFields.calculateNewValue(arguments, event));
    }
}
