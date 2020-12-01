package com.cloudberry.cloudberry.topology.model.deletion;

import com.cloudberry.cloudberry.topology.model.mapping.arguments.EntryMapRecord;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class DeletionExpression {
    private final List<EntryMapRecord> recordsToDelete;
}
