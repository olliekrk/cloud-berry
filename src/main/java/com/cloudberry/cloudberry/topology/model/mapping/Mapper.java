package com.cloudberry.cloudberry.topology.model.mapping;

import com.cloudberry.cloudberry.common.syntax.MapSyntax;
import com.cloudberry.cloudberry.kafka.event.generic.ComputationEvent;
import lombok.val;

public class Mapper {

    public static ComputationEvent calculateNewComputationEvent(
            ComputationEvent event, MappingExpression mappingExpression
    ) {
        for (MappingEvaluation mappingEvaluation : mappingExpression.getMappingEvaluations()) {
            val mapKey = mappingEvaluation.getName();
            val oldValue = switch (mappingEvaluation.getComputationEventMapType()) {
                case FIELDS -> event.getFields().get(mapKey);
                case TAGS -> event.getTags().get(mapKey);
            };

            val newValue = mappingEvaluation.getOperator()
                    .execute(oldValue, mappingEvaluation.getArguments(), event);

            event = switch (mappingEvaluation.getComputationEventMapType()) {
                case FIELDS -> event.withFields(MapSyntax.with(event.getFields(), mapKey, newValue));
                case TAGS -> event
                        .withTags(MapSyntax.with(event.getTags(), mapKey, newValue.toString()));
            };
        }

        return event;
    }
}
