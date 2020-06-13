package com.cloudberry.cloudberry.model.solution;

import com.cloudberry.cloudberry.model.Parametrized;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
public class Solution implements Parametrized<String, Object> {
    public final Map<String, Object> parameters;

    @JsonCreator
    public Solution(@JsonProperty("parameters") Map<String, Object> parameters) {
        this.parameters = parameters;
    }
}
