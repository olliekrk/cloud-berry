package com.cloudberry.cloudberry.topology.model.mapping.operators;

import com.cloudberry.cloudberry.kafka.event.generic.ComputationEvent;
import com.cloudberry.cloudberry.topology.model.mapping.arguments.MappingArgument;

import java.util.List;

public class AddNumberOperator implements MappingOperator<Double, Double, Double> {
    @Override
    public Double getNewValue(
            Double oldValue, List<? extends MappingArgument<Double>> mappingArguments,
            ComputationEvent event
    ) {
        return oldValue + mappingArguments.stream().mapToDouble(MappingArgument::getArgument).sum();
    }
}
