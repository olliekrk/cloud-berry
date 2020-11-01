package com.cloudberry.cloudberry.topology.model.filtering;

import com.cloudberry.cloudberry.topology.model.operators.LogicalOperator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilterExpression {
    private LogicalOperator operator;
    private List<FilterExpression> expressions; // may be empty
    private List<FilterPredicate> predicates;
}
