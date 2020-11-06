package com.cloudberry.cloudberry.topology.model.mapping.arguments;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DoubleArgument implements MappingArgument<Double> {

    private final Double value;

    public DoubleArgument(@JsonProperty("value") Double value) {
        this.value = value;
    }

    @Override
    public Double getArgument() {
        return value;
    }
}
