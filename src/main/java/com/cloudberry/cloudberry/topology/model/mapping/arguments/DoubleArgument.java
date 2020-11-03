package com.cloudberry.cloudberry.topology.model.mapping.arguments;

import lombok.Data;

@Data
public class DoubleArgument implements MappingArgument<Double> {

    private final Double value;

    @Override
    public Double getArgument() {
        return value;
    }
}
