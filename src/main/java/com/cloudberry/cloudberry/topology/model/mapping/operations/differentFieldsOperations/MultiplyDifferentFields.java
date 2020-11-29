package com.cloudberry.cloudberry.topology.model.mapping.operations.differentFieldsOperations;

import com.cloudberry.cloudberry.kafka.event.generic.ComputationEvent;
import com.cloudberry.cloudberry.topology.model.mapping.arguments.EntryMapRecord;
import com.cloudberry.cloudberry.topology.model.mapping.arguments.MappingArgument;

import java.util.List;

public final class MultiplyDifferentFields {
    public static double calculateNewValue(
            List<? extends MappingArgument<EntryMapRecord>> arguments, ComputationEvent event
    ) {
        return EventExtractorUtils.getStreamOfDoubles(arguments, event)
                .mapToDouble(d -> d)
                .reduce((a, b) -> a * b)
                .orElseThrow();
    }
}
