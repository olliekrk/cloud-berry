package com.cloudberry.cloudberry.topology.model.mapping.operations.doubleArgumentOperations;

import com.cloudberry.cloudberry.topology.model.mapping.arguments.MappingArgument;
import com.cloudberry.cloudberry.topology.model.mapping.operations.DivisionByZeroException;

import java.util.List;

public final class DivideDoubles {
    public static Object calculateNewValue(List<? extends MappingArgument<Double>> arguments, Object oldValue) {
        final double divider = MultiplyDoubles.getMultiplyProductOfList(arguments);
        if (divider == 0) {
            throw new DivisionByZeroException();
        }
        return Double.parseDouble(oldValue.toString()) / divider;
    }
}
