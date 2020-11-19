package com.cloudberry.cloudberry.topology.model.mapping.operations.doubleArgumentOperations;

import com.cloudberry.cloudberry.topology.model.mapping.arguments.MappingArgument;

import java.util.List;

public final class MultiplyDoubles {
    public static Object calculateNewValue(List<? extends MappingArgument<Double>> arguments, Object oldValue) {
        return Double.parseDouble(oldValue.toString()) *
                getMultiplyProductOfList(arguments);
    }

    static double getMultiplyProductOfList(List<? extends MappingArgument<Double>> arguments) {
        if (arguments.isEmpty()) { return 0; }
        return arguments.stream()
                .mapToDouble(MappingArgument::getArgument)
                .reduce(1, (a, b) -> a * b);
    }
}
