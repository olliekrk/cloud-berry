package com.cloudberry.cloudberry.topology.model.mapping.operations.differentFieldsOperations;

import com.cloudberry.cloudberry.kafka.event.generic.ComputationEvent;
import com.cloudberry.cloudberry.topology.model.mapping.arguments.EntryMapRecord;
import com.cloudberry.cloudberry.topology.model.mapping.arguments.MappingArgument;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public final class AddDifferentFields {
    private final EventExtractorUtils eventExtractorUtils;

    public double calculateNewValue(
            List<? extends MappingArgument<EntryMapRecord>> arguments, ComputationEvent event
    ) {
        return eventExtractorUtils.getStreamOfDoubles(arguments, event)
                .reduce(Double::sum)
                .orElseThrow();
    }
}
