package com.cloudberry.cloudberry.topology.model.mapping;

import com.cloudberry.cloudberry.common.syntax.MapSyntax;
import com.cloudberry.cloudberry.kafka.event.generic.ComputationEvent;
import com.cloudberry.cloudberry.topology.model.mapping.arguments.EntryMapRecord;
import com.cloudberry.cloudberry.topology.model.mapping.arguments.MappingArgument;
import com.cloudberry.cloudberry.topology.model.mapping.operations.AddDifferentFields;
import com.cloudberry.cloudberry.topology.model.mapping.operations.AddDoubles;
import lombok.val;

import java.util.List;

public class Mapper {

    public static ComputationEvent calculateNewComputationEvent(
            ComputationEvent event, MappingExpression mappingExpression
    ) {
        for (val mappingEvaluation : mappingExpression.getMappingEvaluations()) {
            val mapKey = mappingEvaluation.getName();

            final List<? extends MappingArgument<?>> arguments = mappingEvaluation.getArguments();
            val newValue = switch (mappingEvaluation.getOperator()) {
                case ADD_DOUBLES -> AddDoubles.calculateNewValue(
                        (List<? extends MappingArgument<Double>>) arguments,
                        getOldValue(event, mappingEvaluation, mapKey)
                );
                case ADD_DIFFERENT_FIELDS -> AddDifferentFields.calculateNewValue(
                        (List<? extends MappingArgument<EntryMapRecord>>) arguments, event
                );
            };

            event = switch (mappingEvaluation.getComputationEventMapType()) {
                case FIELDS -> event.withFields(MapSyntax.with(event.getFields(), mapKey, newValue));
                case TAGS -> event
                        .withTags(MapSyntax.with(event.getTags(), mapKey, newValue.toString()));
            };
        }

        return event;
    }

    private static Object getOldValue(ComputationEvent event, MappingEvaluation<?> mappingEvaluation, String mapKey) {
        return switch (mappingEvaluation.getComputationEventMapType()) {
            case FIELDS -> event.getFields().get(mapKey);
            case TAGS -> event.getTags().get(mapKey);
        };
    }
}
