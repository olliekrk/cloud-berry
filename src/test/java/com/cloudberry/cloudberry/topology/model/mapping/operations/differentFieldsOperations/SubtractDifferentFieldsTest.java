package com.cloudberry.cloudberry.topology.model.mapping.operations.differentFieldsOperations;

import com.cloudberry.cloudberry.kafka.event.generic.ComputationEvent;
import com.cloudberry.cloudberry.topology.model.mapping.arguments.EntryMapArgument;
import com.cloudberry.cloudberry.topology.model.mapping.arguments.EntryMapRecord;
import lombok.val;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static com.cloudberry.cloudberry.topology.model.ComputationEventMapType.FIELDS;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SubtractDifferentFieldsTest {

    @Test
    void properSubtractLotOfValues() {
        val salaryKey = "velocity";
        val zusKey = "zus";
        val dochodowyKey = "dochodowy";
        val salary = 15000;
        val zus = 2000;
        val dochodowy = 3000;

        val arguments = List.of(
                new EntryMapArgument(new EntryMapRecord(FIELDS, salaryKey)),
                new EntryMapArgument(new EntryMapRecord(FIELDS, zusKey)),
                new EntryMapArgument(new EntryMapRecord(FIELDS, dochodowyKey))
        );
        val event = new ComputationEvent(
                Instant.ofEpochMilli(0),
                "some-test-measurement-name",
                Map.of(salaryKey, salary, zusKey, zus, dochodowyKey, dochodowy),
                Map.of()
        );

        val calculatedDistance = SubtractDifferentFields.calculateNewValue(arguments, event);

        val expectedDistance = (double) salary - zus - dochodowy;
        assertEquals(expectedDistance, calculatedDistance);
    }

}
