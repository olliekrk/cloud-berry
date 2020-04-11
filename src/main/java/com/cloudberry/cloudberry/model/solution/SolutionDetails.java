package com.cloudberry.cloudberry.model.solution;

import lombok.Data;

@Data
public class SolutionDetails {
    public final long workplaceId;
    public final long stepNumber;
    public final long occurrencesCount;

    public SolutionDetails(long workplaceId, long stepsCount, long occurrences) {
        this.workplaceId = workplaceId;
        this.stepNumber = stepsCount;
        this.occurrencesCount = occurrences;
    }
}
