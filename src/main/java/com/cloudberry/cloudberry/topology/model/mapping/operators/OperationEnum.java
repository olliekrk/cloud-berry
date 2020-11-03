package com.cloudberry.cloudberry.topology.model.mapping.operators;

import com.cloudberry.cloudberry.kafka.event.generic.ComputationEvent;
import com.cloudberry.cloudberry.topology.model.mapping.arguments.DoubleArgument;
import com.cloudberry.cloudberry.topology.model.mapping.arguments.MappingArgument;
import lombok.val;

import java.util.List;
import java.util.stream.Collectors;

public enum OperationEnum {
    ADD_DOUBLES {
        @Override
        public Object execute(
                Object oldValue, List<MappingArgument<?>> arguments,
                ComputationEvent event
        ) {
            val doubleArguments = arguments.stream()
                    .map(objectMappingArgument -> new DoubleArgument(
                            (Double) objectMappingArgument.getArgument())).collect(
                            Collectors.toList());

            return new AddNumberOperator()
                    .getNewValue(Double.valueOf(oldValue.toString()), doubleArguments, event);
        }
    };


    public abstract Object execute(
            Object oldValue, List<MappingArgument<?>> arguments,
            ComputationEvent event
    );
}
