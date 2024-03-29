package com.cloudberry.cloudberry.topology.model.filtering;

import com.cloudberry.cloudberry.topology.model.operators.EqualityOperator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilterPredicate {
    private String name;
    private String value;
    private EqualityOperator operator;
    private FilterType type;
    private boolean isField; // by default filter is applied to tags, not fields
}
