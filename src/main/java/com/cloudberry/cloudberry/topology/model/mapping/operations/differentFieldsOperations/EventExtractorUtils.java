package com.cloudberry.cloudberry.topology.model.mapping.operations.differentFieldsOperations;

import com.cloudberry.cloudberry.kafka.event.generic.ComputationEvent;
import com.cloudberry.cloudberry.rest.util.IdDispatcher;
import com.cloudberry.cloudberry.topology.model.mapping.arguments.EntryMapRecord;
import com.cloudberry.cloudberry.topology.model.mapping.arguments.MappingArgument;
import com.cloudberry.cloudberry.topology.model.mapping.operations.ComputationMetaParameterExtractor;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.cloudberry.cloudberry.db.influx.InfluxDefaults.CommonTags.COMPUTATION_ID;
import static java.lang.String.format;

@Slf4j
@Component
@RequiredArgsConstructor
public final class EventExtractorUtils {
    private final ComputationMetaParameterExtractor computationMetaParameterExtractor;

    public Stream<Double> getStreamOfDoubles(
            List<? extends MappingArgument<EntryMapRecord>> arguments,
            ComputationEvent event
    ) {
        return arguments.stream().map(MappingArgument::getArgument)
                .map(entryMapRecord -> {
                    val mapKey = entryMapRecord.getMapKey();
                    return switch (entryMapRecord.getMapType()) {
                        case FIELDS -> event.getFields().get(mapKey);
                        case TAGS -> event.getTags().get(mapKey);
                        case METADATA -> computationMetaParameterExtractor.getValueFromMetaParametersMap(
                                getComputationId(event),
                                mapKey
                        );
                    };
                })
                .flatMap(valueToAdd ->
                                 Try.of(() -> Stream.of(Double.valueOf(valueToAdd.toString())))
                                         .getOrElse(() -> {
                                             log.warn("Cannot parse value: {} to double. Skipping it.", valueToAdd);
                                             return Stream.empty();
                                         }));
    }

    private ObjectId getComputationId(ComputationEvent event) {
        final String computationIdHex = event.getTags().get(COMPUTATION_ID);
        return Optional.ofNullable(computationIdHex)
                .map(a -> Try.of(() -> IdDispatcher.getComputationId(a)).get())
                .orElseThrow(
                        () -> new IllegalArgumentException(
                                format(
                                        "You need to provide proper id of computation in event tags under the key: %s",
                                        COMPUTATION_ID
                                )));
    }
}
