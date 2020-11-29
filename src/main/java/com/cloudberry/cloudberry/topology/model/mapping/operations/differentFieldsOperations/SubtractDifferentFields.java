package com.cloudberry.cloudberry.topology.model.mapping.operations.differentFieldsOperations;

import com.cloudberry.cloudberry.kafka.event.generic.ComputationEvent;
import com.cloudberry.cloudberry.topology.model.mapping.arguments.EntryMapRecord;
import com.cloudberry.cloudberry.topology.model.mapping.arguments.MappingArgument;
import lombok.val;

import java.util.List;
import java.util.stream.Collectors;

public final class SubtractDifferentFields {
    public static double calculateNewValue(
            List<? extends MappingArgument<EntryMapRecord>> arguments, ComputationEvent event
    ) {
        val allArguments = EventExtractorUtils.getStreamOfDoubles(arguments, event).collect(Collectors.toList());
        val subtrahend = allArguments.stream().skip(1).reduce(Double::sum).orElseThrow();
        return allArguments.stream().findFirst().orElseThrow() - subtrahend;
    }
}
