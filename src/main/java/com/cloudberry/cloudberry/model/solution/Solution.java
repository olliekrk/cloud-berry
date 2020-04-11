package com.cloudberry.cloudberry.model.solution;

import com.cloudberry.cloudberry.model.Parametrized;

import java.util.Map;

public class Solution implements Parametrized<String, Object> {
    private final Map<String, Object> parameters;

    public Solution(Map<String, Object> parameters) {
        this.parameters = parameters;
    }

    @Override
    public Map<String, Object> getParameters() {
        return parameters;
    }
}
