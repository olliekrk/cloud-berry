package com.cloudberry.cloudberry.topology.model.mapping.operations.doubleArgumentOperations;

import com.cloudberry.cloudberry.topology.model.mapping.arguments.MappingArgument;

import java.util.List;

public final class AddDoubles {
    public static Object calculateNewValue(List<? extends MappingArgument<Double>> arguments, Object oldValue) {
//        List<? extends MappingArgument<Double>> mappingArguments = toDoubleArguments(arguments);
        return Double.parseDouble(oldValue.toString()) +
                arguments.stream().mapToDouble(MappingArgument::getArgument).sum();
    }

//    @NotNull
//    private static List<DoubleArgument> toDoubleArguments(List<MappingArgument<?>> arguments) {
//        return arguments.stream()
//                .map(mappingArgument -> new DoubleArgument((Double) (mappingArgument.getArgument())))
//                .collect(Collectors.toList());
//    }

}
