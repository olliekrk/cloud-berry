package com.cloudberry.cloudberry.topology.model.deletion;

import com.cloudberry.cloudberry.kafka.event.generic.ComputationEvent;
import com.cloudberry.cloudberry.topology.model.ComputationEventMapType;
import com.cloudberry.cloudberry.topology.model.mapping.arguments.EntryMapRecord;
import lombok.val;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DeletionEvaluatorTest {

    private static Stream<Arguments> createNewFieldSource() {
        return Stream.of(
                // remove single key from fields
                Arguments.of(
                        Map.of("key", 1), Map.of(),
                        List.of(new EntryMapRecord(ComputationEventMapType.FIELDS, "key")),
                        Map.of(), Map.of()
                ),
                // remove single key from tags
                Arguments.of(
                        Map.of(), Map.of("tag1", "aa"),
                        List.of(new EntryMapRecord(ComputationEventMapType.TAGS, "tag1")),
                        Map.of(), Map.of()
                ),
                // remove not existing key from tags
                Arguments.of(
                        Map.of(), Map.of("tag1", "aa"),
                        List.of(new EntryMapRecord(ComputationEventMapType.TAGS, "tag-not-existing")),
                        Map.of(), Map.of("tag1", "aa")
                ),
                // remove same element twice
                Arguments.of(
                        Map.of("key", 1, "anotherKey", 2), Map.of(),
                        List.of(
                                new EntryMapRecord(ComputationEventMapType.FIELDS, "key"),
                                new EntryMapRecord(ComputationEventMapType.FIELDS, "key")
                        ),
                        Map.of("anotherKey", 2), Map.of()
                ),
                // remove multiple from fields and tags
                Arguments.of(
                        Map.of("field-preserve", 1, "field-remove-2", 2, "field-remove-3", 3),
                        Map.of("tag-preserve", "1", "tag-remove-2", "2", "tag-remove-3", "3"),
                        List.of(
                                new EntryMapRecord(ComputationEventMapType.FIELDS, "field-remove-2"),
                                new EntryMapRecord(ComputationEventMapType.FIELDS, "field-remove-3"),
                                new EntryMapRecord(ComputationEventMapType.TAGS, "tag-remove-2"),
                                new EntryMapRecord(ComputationEventMapType.TAGS, "tag-remove-3")
                        ),
                        Map.of("field-preserve", 1), Map.of("tag-preserve", "1")
                )
        );
    }


    @ParameterizedTest
    @MethodSource("createNewFieldSource")
    void deleteElementsFromEventTest(
            Map<String, Object> inputFields, Map<String, String> inputTags,
            List<EntryMapRecord> recordsToRemove,
            Map<String, Object> expectedFields, Map<String, String> expectedTags
    ) {
        val event = new ComputationEvent(
                Instant.ofEpochMilli(0),
                "some-test-measurement-name-deletion",
                inputFields,
                inputTags
        );

        val newEvent = DeletionEvaluator.calculateNewComputationEvent(
                event,
                new DeletionExpression(recordsToRemove)
        );

        assertEquals(expectedFields, newEvent.getFields());
        assertEquals(expectedTags, newEvent.getTags());
    }
}
