package com.cloudberry.cloudberry.analytics.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum OptimizationGoal {
    @JsonProperty("max") MAX,
    @JsonProperty("min") MIN
}
