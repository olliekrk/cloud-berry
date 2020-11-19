package com.cloudberry.cloudberry.topology.model.mapping.operations.doubleArgumentOperations;

import com.cloudberry.cloudberry.topology.model.mapping.arguments.MappingArgument;

import java.util.List;

public final class SubtractDoubles {
    public static Object calculateNewValue(List<? extends MappingArgument<Double>> arguments, Object oldValue) {
        return Double.parseDouble(oldValue.toString()) -
                arguments.stream().mapToDouble(MappingArgument::getArgument).sum();
    }
}
