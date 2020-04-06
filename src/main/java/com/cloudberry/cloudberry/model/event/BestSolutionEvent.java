package com.cloudberry.cloudberry.model.event;

import com.cloudberry.cloudberry.model.solution.Solution;

import java.util.Map;

public class BestSolutionEvent extends TimedEvent {
    public final Map<Long, Solution> solutions;

    public BestSolutionEvent(Map<Long, Solution> solutions) {
        this.solutions = solutions;
    }
}
