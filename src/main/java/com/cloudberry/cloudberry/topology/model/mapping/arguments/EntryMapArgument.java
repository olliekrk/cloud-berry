package com.cloudberry.cloudberry.topology.model.mapping.arguments;

import lombok.Data;

@Data
public class EntryMapArgument implements MappingArgument<EntryMapRecord> {

    private final EntryMapRecord value;

    @Override
    public EntryMapRecord getArgument() {
        return value;
    }

}
