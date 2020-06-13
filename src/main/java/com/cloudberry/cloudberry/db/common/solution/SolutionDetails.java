package com.cloudberry.cloudberry.db.common.solution;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SolutionDetails {
    public final long workplaceId;
    public final long stepNumber;
    public final long occurrencesCount;

    @JsonCreator
    public SolutionDetails(
            @JsonProperty("workplaceId") long workplaceId,
            @JsonProperty("stepNumber") long stepNumber,
            @JsonProperty("occurrencesCount") long occurrencesCount) {
        this.workplaceId = workplaceId;
        this.stepNumber = stepNumber;
        this.occurrencesCount = occurrencesCount;
    }
}
