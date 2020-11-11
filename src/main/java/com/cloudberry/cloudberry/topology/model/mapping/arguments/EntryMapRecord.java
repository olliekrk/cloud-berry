package com.cloudberry.cloudberry.topology.model.mapping.arguments;

import com.cloudberry.cloudberry.topology.model.ComputationEventMapType;
import lombok.Value;

@Value
public class EntryMapRecord {
    ComputationEventMapType mapType;
    String mapKey;
}
