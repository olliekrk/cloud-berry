package com.cloudberry.cloudberry.model.event;

import com.cloudberry.cloudberry.model.solution.Solution;
import com.cloudberry.cloudberry.model.solution.SolutionDetails;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.UUID;

public class BestSolutionEvent extends Event {
    public final Solution solution;
    public final SolutionDetails details;

    public BestSolutionEvent(UUID evaluationId, Solution solution, SolutionDetails details) {
        super(evaluationId);
        this.solution = solution;
        this.details = details;
    }

    @JsonCreator
    private BestSolutionEvent(@JsonProperty("evaluationId") UUID evaluationId,
                              @JsonProperty("time") Instant time,
                              @JsonProperty("solution") Solution solution,
                              @JsonProperty("details") SolutionDetails details) {
        super(evaluationId, time);
        this.solution = solution;
        this.details = details;
    }
}
