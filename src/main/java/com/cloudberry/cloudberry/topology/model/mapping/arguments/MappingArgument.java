package com.cloudberry.cloudberry.topology.model.mapping.arguments;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT)
@JsonSubTypes({
        @JsonSubTypes.Type(value = DoubleArgument.class),
        @JsonSubTypes.Type(value = EntryMapArgument.class)
})
public interface MappingArgument<T> {
    @JsonIgnore
    T getArgument();
}
