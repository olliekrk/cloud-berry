package com.cloudberry.cloudberry.topology.model.mapping.operations;

import com.cloudberry.cloudberry.kafka.event.generic.ComputationEvent;
import com.cloudberry.cloudberry.topology.model.mapping.arguments.EntryMapRecord;
import com.cloudberry.cloudberry.topology.model.mapping.arguments.MappingArgument;
import io.vavr.control.Try;

import java.util.List;
import java.util.stream.Stream;

public class AddDifferentFields {
    public static Object calculateNewValue(
            List<? extends MappingArgument<EntryMapRecord>> arguments, ComputationEvent event
    ) {
        return arguments.stream().map(MappingArgument::getArgument)
                .map(entryMapRecord -> switch (entryMapRecord.getMapType()) {
                    case FIELDS -> event.getFields().get(entryMapRecord.getMapKey());
                    case TAGS -> event.getTags().get(entryMapRecord.getMapKey());
                })
                .flatMap(valueToAdd -> Try.of(() -> Stream.of(Double.valueOf(valueToAdd.toString())))
                        .getOrElse(Stream.empty()))
                .mapToDouble(d -> d)
                .sum();
    }
}
