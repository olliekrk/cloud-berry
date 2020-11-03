package com.cloudberry.cloudberry.topology.model.mapping.operators;

import com.cloudberry.cloudberry.kafka.event.generic.ComputationEvent;
import com.cloudberry.cloudberry.topology.model.mapping.arguments.MappingArgument;

import java.util.List;

/**
 * @param <O> oldValue type
 * @param <R> return value type
 * @param <A> arguments type
 */
public interface MappingOperator<O, R, A> {
    R getNewValue(O oldValue, List<? extends MappingArgument<A>> arguments, ComputationEvent event);
}
