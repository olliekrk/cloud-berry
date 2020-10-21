package com.cloudberry.cloudberry.model.solution;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SolutionDetails {
    private final long workplaceId;
    private final long stepNumber;
    private final long occurrencesCount;

    @JsonCreator
    public SolutionDetails(
            @JsonProperty("workplaceId") long workplaceId,
            @JsonProperty("stepNumber") long stepNumber,
            @JsonProperty("occurrencesCount") long occurrencesCount
    ) {
        this.workplaceId = workplaceId;
        this.stepNumber = stepNumber;
        this.occurrencesCount = occurrencesCount;
    }
}
