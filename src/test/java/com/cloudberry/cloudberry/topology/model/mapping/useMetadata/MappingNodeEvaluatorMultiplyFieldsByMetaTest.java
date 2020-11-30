package com.cloudberry.cloudberry.topology.model.mapping.useMetadata;

import com.cloudberry.cloudberry.kafka.event.generic.ComputationEvent;
import com.cloudberry.cloudberry.topology.model.mapping.MappingEvaluation;
import com.cloudberry.cloudberry.topology.model.mapping.MappingExpression;
import com.cloudberry.cloudberry.topology.model.mapping.arguments.EntryMapArgument;
import com.cloudberry.cloudberry.topology.model.mapping.arguments.EntryMapRecord;
import com.cloudberry.cloudberry.topology.model.mapping.operators.OperationEnum;
import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static com.cloudberry.cloudberry.db.influx.InfluxDefaults.CommonTags.COMPUTATION_ID;
import static com.cloudberry.cloudberry.db.mongo.service.DataGenerator.COMPUTATION_ID_1_A_A;
import static com.cloudberry.cloudberry.db.mongo.service.DataGenerator.CONFIGURATION_1_A_PRICE_KEY;
import static com.cloudberry.cloudberry.db.mongo.service.DataGenerator.CONFIGURATION_1_A_PRICE_VALUE;
import static com.cloudberry.cloudberry.topology.model.ComputationEventMapType.FIELDS;
import static com.cloudberry.cloudberry.topology.model.ComputationEventMapType.METADATA;

public class MappingNodeEvaluatorMultiplyFieldsByMetaTest extends MappingNodeEvaluatorUseMetadataTestBase {

    @Test
    void multiplyValueFromFieldsByValueFromMeta() {
        val fitnessKey = "fitness";
        val newFieldInEventName = "CALCULATED";
        val fitnessValue = 1000;
        val incomingEvent = new ComputationEvent(
                Instant.ofEpochMilli(0),
                "some-test-measurement-name",
                Map.of(fitnessKey, fitnessValue),
                Map.of(COMPUTATION_ID, COMPUTATION_ID_1_A_A.toHexString())
        );

        val newEvent = mappingNodeEvaluator.calculateNewComputationEvent(incomingEvent, new MappingExpression(
                List.of(new MappingEvaluation<>(
                                newFieldInEventName,
                                FIELDS,
                                OperationEnum.MULTIPLY_DIFFERENT_FIELDS,
                                List.of(
                                        new EntryMapArgument(new EntryMapRecord(FIELDS, fitnessKey)),
                                        new EntryMapArgument(new EntryMapRecord(METADATA, CONFIGURATION_1_A_PRICE_KEY))
                                )
                        )
                )));

        Map<String, Object> expectedFields = Map.of(fitnessKey, fitnessValue, newFieldInEventName,
                                                    (double) fitnessValue * CONFIGURATION_1_A_PRICE_VALUE
        );

        Assertions.assertEquals(expectedFields, newEvent.getFields());
    }
}
