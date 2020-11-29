package com.cloudberry.cloudberry.topology.model.mapping.operations.differentFieldsOperations;

import com.cloudberry.cloudberry.kafka.event.generic.ComputationEvent;
import com.cloudberry.cloudberry.topology.model.mapping.arguments.EntryMapRecord;
import com.cloudberry.cloudberry.topology.model.mapping.arguments.MappingArgument;

import java.util.List;

public final class AddDifferentFields {
    public static double calculateNewValue(
            List<? extends MappingArgument<EntryMapRecord>> arguments, ComputationEvent event
    ) {
        return EventExtractorUtils.getStreamOfDoubles(arguments, event)
                .reduce(Double::sum)
                .orElseThrow();
    }
}
