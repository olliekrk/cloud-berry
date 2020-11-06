package com.cloudberry.cloudberry.topology.model.mapping.operations;

import com.cloudberry.cloudberry.topology.model.mapping.arguments.DoubleArgument;
import com.cloudberry.cloudberry.topology.model.mapping.arguments.MappingArgument;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public class AddDoubles {
    public static Object calculateNewValue(List<? extends MappingArgument<Double>> arguments, Object oldValue) {
//        List<? extends MappingArgument<Double>> mappingArguments = toDoubleArguments(arguments);
        return (Double) oldValue + arguments.stream().mapToDouble(MappingArgument::getArgument).sum();
    }

//    @NotNull
//    private static List<DoubleArgument> toDoubleArguments(List<MappingArgument<?>> arguments) {
//        return arguments.stream()
//                .map(mappingArgument -> new DoubleArgument((Double) (mappingArgument.getArgument())))
//                .collect(Collectors.toList());
//    }

}
