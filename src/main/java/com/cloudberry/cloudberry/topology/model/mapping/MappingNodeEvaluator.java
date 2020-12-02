package com.cloudberry.cloudberry.topology.model.mapping;

import com.cloudberry.cloudberry.common.syntax.MapSyntax;
import com.cloudberry.cloudberry.kafka.event.generic.ComputationEvent;
import com.cloudberry.cloudberry.rest.exceptions.invalid.id.InvalidComputationIdException;
import com.cloudberry.cloudberry.topology.model.mapping.arguments.EntryMapRecord;
import com.cloudberry.cloudberry.topology.model.mapping.arguments.MappingArgument;
import com.cloudberry.cloudberry.topology.model.mapping.operations.differentFieldsOperations.AddDifferentFields;
import com.cloudberry.cloudberry.topology.model.mapping.operations.differentFieldsOperations.DivideDifferentFields;
import com.cloudberry.cloudberry.topology.model.mapping.operations.differentFieldsOperations.MultiplyDifferentFields;
import com.cloudberry.cloudberry.topology.model.mapping.operations.differentFieldsOperations.SubtractDifferentFields;
import com.cloudberry.cloudberry.topology.model.mapping.operations.doubleArgumentOperations.AddDoubles;
import com.cloudberry.cloudberry.topology.model.mapping.operations.doubleArgumentOperations.DivideDoubles;
import com.cloudberry.cloudberry.topology.model.mapping.operations.doubleArgumentOperations.MultiplyDoubles;
import com.cloudberry.cloudberry.topology.model.mapping.operations.doubleArgumentOperations.SubtractDoubles;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class MappingNodeEvaluator {
    private final AddDifferentFields addDifferentFields;
    private final SubtractDifferentFields subtractDifferentFields;
    private final MultiplyDifferentFields multiplyDifferentFields;
    private final DivideDifferentFields divideDifferentFields;

    public ComputationEvent calculateNewComputationEvent(
            ComputationEvent event, MappingExpression mappingExpression
    ) {
        for (val mappingEvaluation : mappingExpression.getMappingEvaluations()) {
            val mapKey = mappingEvaluation.getName();

            final List<? extends MappingArgument<?>> arguments = mappingEvaluation.getArguments();
            val oldValue = getOldValue(event, mappingEvaluation);
            val newValue = switch (mappingEvaluation.getOperator()) {
                case ADD_DOUBLES -> AddDoubles.calculateNewValue(
                        (List<? extends MappingArgument<Double>>) arguments,
                        oldValue
                );
                case SUBTRACT_DOUBLES -> SubtractDoubles
                        .calculateNewValue(
                                (List<? extends MappingArgument<Double>>) arguments,
                                oldValue
                        );
                case MULTIPLY_DOUBLES -> MultiplyDoubles.calculateNewValue(
                        (List<? extends MappingArgument<Double>>) arguments,
                        oldValue
                );
                case DIVIDE_DOUBLES -> DivideDoubles.calculateNewValue(
                        (List<? extends MappingArgument<Double>>) arguments,
                        oldValue
                );
                case ADD_DIFFERENT_FIELDS -> addDifferentFields.calculateNewValue(
                        (List<? extends MappingArgument<EntryMapRecord>>) arguments,
                        event
                );
                case SUBTRACT_DIFFERENT_FIELDS -> subtractDifferentFields.calculateNewValue(
                        (List<? extends MappingArgument<EntryMapRecord>>) arguments,
                        event
                );
                case MULTIPLY_DIFFERENT_FIELDS -> multiplyDifferentFields.calculateNewValue(
                        (List<? extends MappingArgument<EntryMapRecord>>) arguments,
                        event
                );
                case DIVIDE_DIFFERENT_FIELDS -> divideDifferentFields.calculateNewValue(
                        (List<? extends MappingArgument<EntryMapRecord>>) arguments,
                        event
                );
            };

            event = switch (mappingEvaluation.getComputationEventMapType()) {
                case FIELDS -> event.withFields(MapSyntax.with(event.getFields(), mapKey, newValue));
                case TAGS -> event
                        .withTags(MapSyntax.with(event.getTags(), mapKey, newValue.toString()));
                case METADATA -> throw new UnsupportedOperationException(); // we cannot update metadata by nodes
            };
        }

        return event;
    }

    private static Object getOldValue(ComputationEvent event, MappingEvaluation<?> mappingEvaluation) {
        val mapKey = mappingEvaluation.getName();
        return switch (mappingEvaluation.getComputationEventMapType()) {
            case FIELDS -> event.getFields().get(mapKey);
            case TAGS -> event.getTags().get(mapKey);
            case METADATA -> throw new UnsupportedOperationException(); // we cannot update metadata by nodes
        };
    }
}