package com.cloudberry.cloudberry.topology.model.mapping.arguments;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class EntryMapArgument implements MappingArgument<EntryMapRecord> {

    private final EntryMapRecord value;

    public EntryMapArgument(@JsonProperty("value") EntryMapRecord value) {
        this.value = value;
    }

    @Override
    public EntryMapRecord getArgument() {
        return value;
    }

}
