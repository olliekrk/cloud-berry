package com.cloudberry.cloudberry.model.event;

import com.cloudberry.cloudberry.model.solution.Solution;
import com.cloudberry.cloudberry.model.solution.SolutionDetails;

import java.util.UUID;

public class BestSolutionEvent extends Event {
    public final Solution solution;
    public final SolutionDetails details;

    public BestSolutionEvent(UUID evaluationId, Solution solution, SolutionDetails details) {
        super(evaluationId);
        this.solution = solution;
        this.details = details;
    }
}
