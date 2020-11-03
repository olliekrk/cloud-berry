package com.cloudberry.cloudberry.topology.model.mapping;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MappingExpression {
    private List<MappingEvaluation> mappingEvaluations;

}
