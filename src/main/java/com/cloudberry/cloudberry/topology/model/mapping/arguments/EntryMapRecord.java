package com.cloudberry.cloudberry.topology.model.mapping.arguments;

import com.cloudberry.cloudberry.topology.model.ComputationEventMapType;
import com.fasterxml.jackson.annotation.JsonProperty;

public record EntryMapRecord(@JsonProperty("mapType") ComputationEventMapType mapType,
                             @JsonProperty("mapKey") String mapKey) {}
