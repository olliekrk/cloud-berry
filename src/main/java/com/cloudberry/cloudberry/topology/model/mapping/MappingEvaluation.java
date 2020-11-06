package com.cloudberry.cloudberry.topology.model.mapping;

import com.cloudberry.cloudberry.topology.model.ComputationEventMapType;
import com.cloudberry.cloudberry.topology.model.mapping.arguments.MappingArgument;
import com.cloudberry.cloudberry.topology.model.mapping.operators.OperationEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MappingEvaluation<T> {
    private String name;
    private ComputationEventMapType computationEventMapType;
    private OperationEnum operator;
    private List<MappingArgument<T>> arguments;
}
