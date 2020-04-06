package com.cloudberry.cloudberry.model.solution;

import java.util.Map;

public class Solution {
    public final Map<String, Double> parameters;
    public final Long workplaceId;
    public final Long stepsCount;
    public final Long occurrences;

    public Solution(Map<String, Double> parameters, Long workplaceId, Long stepsCount, Long occurrences) {
        this.parameters = parameters;
        this.workplaceId = workplaceId;
        this.stepsCount = stepsCount;
        this.occurrences = occurrences;
    }
}
