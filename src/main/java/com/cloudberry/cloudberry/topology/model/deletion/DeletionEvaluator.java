package com.cloudberry.cloudberry.topology.model.deletion;

import com.cloudberry.cloudberry.kafka.event.generic.ComputationEvent;
import lombok.val;

import java.util.HashMap;

public final class DeletionEvaluator {
    public static ComputationEvent calculateNewComputationEvent(
            ComputationEvent event, DeletionExpression deletionExpression
    ) {
        val oldFieldsMap = new HashMap<>(event.getFields());
        val oldTagsMap = new HashMap<>(event.getTags());
        deletionExpression.getRecordsToDelete()
                .forEach(entryMapRecord -> {
                    val mapKey = entryMapRecord.getMapKey();
                    switch (entryMapRecord.getMapType()) {
                    case FIELDS -> oldFieldsMap.remove(mapKey);
                    case TAGS -> oldTagsMap.remove(mapKey);
                    case METADATA -> throw new UnsupportedOperationException(); // we cannot delete metadata by nodes
                    }
                });

        return event.withFields(oldFieldsMap).withTags(oldTagsMap);
    }
}
