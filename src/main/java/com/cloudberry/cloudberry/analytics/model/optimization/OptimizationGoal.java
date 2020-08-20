package com.cloudberry.cloudberry.analytics.model.optimization;

public enum OptimizationGoal {
    MAX,
    MIN,
    ;

    public static boolean isMaximumSearched(OptimizationGoal other) {
        return MAX.equals(other);
    }
}
