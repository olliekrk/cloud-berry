package com.cloudberry.cloudberry.topology.model.filtering;

import com.cloudberry.cloudberry.topology.model.operators.LogicalOperator;
import lombok.Data;

import java.util.List;

@Data
public class FilterExpression {
    private LogicalOperator operator;
    private List<FilterExpression> expressions; // may be empty
    private List<FilterPredicate> predicates;
}
