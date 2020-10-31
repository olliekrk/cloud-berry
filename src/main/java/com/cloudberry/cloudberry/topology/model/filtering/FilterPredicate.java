package com.cloudberry.cloudberry.topology.model.filtering;

import com.cloudberry.cloudberry.topology.model.operators.Operator;
import lombok.Data;

@Data
public class FilterPredicate {
    String name;
    String value;
    Operator operator;
    FilterType type;
    boolean isField; // by default filter is applied to tags, not fields
}
