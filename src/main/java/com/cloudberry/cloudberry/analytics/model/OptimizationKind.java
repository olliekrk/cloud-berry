package com.cloudberry.cloudberry.analytics.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum OptimizationKind {
    @JsonProperty("final") FINAL_VALUE,
    @JsonProperty("area") AREA_UNDER_CURVE // used to estimate e.g. convergence
}
