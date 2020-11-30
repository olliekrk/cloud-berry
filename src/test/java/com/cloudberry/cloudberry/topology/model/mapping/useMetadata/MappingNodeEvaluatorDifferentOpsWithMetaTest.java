package com.cloudberry.cloudberry.topology.model.mapping.useMetadata;

import com.cloudberry.cloudberry.kafka.event.generic.ComputationEvent;
import com.cloudberry.cloudberry.topology.model.mapping.MappingEvaluation;
import com.cloudberry.cloudberry.topology.model.mapping.MappingExpression;
import com.cloudberry.cloudberry.topology.model.mapping.arguments.DoubleArgument;
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

public class MappingNodeEvaluatorDifferentOpsWithMetaTest extends MappingNodeEvaluatorUseMetadataTestBase {
    @Test
    void multiplyValueFromFieldsByValueFromMeta() {
        val energyKey = "energy";
        val secondPowerOfParam = "secondPower";
        val doubledEnergyKey = "doubledEnergy";
        val meritFactorKey = "meritFactor";
        val energyValue = 1000;
        val incomingEvent = new ComputationEvent(
                Instant.ofEpochMilli(0),
                "some-test-measurement-name-merit-calc",
                Map.of(energyKey, energyValue),
                Map.of(COMPUTATION_ID, COMPUTATION_ID_1_A_A.toHexString())
        );

        val newEvent = mappingNodeEvaluator.calculateNewComputationEvent(incomingEvent, new MappingExpression(
                List.of(
                        // calculate second power of param
                        new MappingEvaluation<>(
                                secondPowerOfParam,
                                FIELDS,
                                OperationEnum.MULTIPLY_DIFFERENT_FIELDS,
                                List.of(
                                        new EntryMapArgument(new EntryMapRecord(METADATA, CONFIGURATION_1_A_PRICE_KEY)),
                                        new EntryMapArgument(new EntryMapRecord(METADATA, CONFIGURATION_1_A_PRICE_KEY))
                                )
                        ),
                        // copy value o energy to "doubledEnergy" field
                        new MappingEvaluation<>(
                                doubledEnergyKey,
                                FIELDS,
                                OperationEnum.ADD_DIFFERENT_FIELDS,
                                List.of(
                                        new EntryMapArgument(new EntryMapRecord(FIELDS, energyKey))
                                )
                        ),
                        // multiple energy by 2
                        new MappingEvaluation<>(
                                doubledEnergyKey,
                                FIELDS,
                                OperationEnum.MULTIPLY_DOUBLES,
                                List.of(new DoubleArgument(2.0))
                        ),
                        // calculate merit value by dividing
                        new MappingEvaluation<>(
                                meritFactorKey,
                                FIELDS,
                                OperationEnum.DIVIDE_DIFFERENT_FIELDS,
                                List.of(
                                        new EntryMapArgument(new EntryMapRecord(FIELDS, secondPowerOfParam)),
                                        new EntryMapArgument(new EntryMapRecord(FIELDS, doubledEnergyKey))
                                )
                        )

                )));

        val secondPowerOfParamValue = (double) CONFIGURATION_1_A_PRICE_VALUE * CONFIGURATION_1_A_PRICE_VALUE;
        val doubledEnergyValue = (double) energyValue * 2;
        Map<String, Object> expectedFields = Map.of(
                energyKey, energyValue,
                secondPowerOfParam, secondPowerOfParamValue,
                doubledEnergyKey, doubledEnergyValue,
                meritFactorKey, secondPowerOfParamValue / doubledEnergyValue
        );

        Assertions.assertEquals(expectedFields, newEvent.getFields());
    }
}
