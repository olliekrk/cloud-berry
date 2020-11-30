package com.cloudberry.cloudberry.topology.model.mapping.operations.differentFieldsOperations;

import com.cloudberry.cloudberry.kafka.event.generic.ComputationEvent;
import com.cloudberry.cloudberry.topology.model.mapping.arguments.EntryMapRecord;
import com.cloudberry.cloudberry.topology.model.mapping.arguments.MappingArgument;
import com.cloudberry.cloudberry.topology.model.mapping.operations.DivisionByZeroException;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public final class DivideDifferentFields {
    private final EventExtractorUtils eventExtractorUtils;

    /**
     * divides first element of args list by multiplication product of the rest
     */
    public double calculateNewValue(
            List<? extends MappingArgument<EntryMapRecord>> arguments, ComputationEvent event
    ) {
        val allArguments = eventExtractorUtils.getStreamOfDoubles(arguments, event).collect(Collectors.toList());
        val divider = allArguments.stream().skip(1).reduce((a, b) -> a * b).orElseThrow();
        if (divider == 0) {
            throw new DivisionByZeroException();
        }
        return allArguments.stream().findFirst().orElseThrow() / divider;
    }
}
