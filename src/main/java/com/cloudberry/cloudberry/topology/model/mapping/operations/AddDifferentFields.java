package com.cloudberry.cloudberry.topology.model.mapping.operations;

import com.cloudberry.cloudberry.kafka.event.generic.ComputationEvent;
import com.cloudberry.cloudberry.topology.model.mapping.arguments.EntryMapRecord;
import com.cloudberry.cloudberry.topology.model.mapping.arguments.MappingArgument;
import io.vavr.control.Try;
import lombok.val;

import java.util.List;
import java.util.stream.Stream;

public final class AddDifferentFields {
    public static Object calculateNewValue(
            List<? extends MappingArgument<EntryMapRecord>> arguments, ComputationEvent event
    ) {
        return arguments.stream().map(MappingArgument::getArgument)
                .map(entryMapRecord -> {
                    val mapKey = entryMapRecord.getMapKey();
                    return switch (entryMapRecord.getMapType()) {
                        case FIELDS -> event.getFields().get(mapKey);
                        case TAGS -> event.getTags().get(mapKey);
                    };
                })
                .flatMap(valueToAdd -> Try.of(() -> Stream.of(Double.valueOf(valueToAdd.toString())))
                        .getOrElse(Stream.empty()))
                .mapToDouble(d -> d)
                .sum();
    }
}
