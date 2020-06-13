package com.cloudberry.cloudberry.kafka.event;

import com.cloudberry.cloudberry.model.solution.Solution;
import com.cloudberry.cloudberry.model.solution.SolutionDetails;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class BestSolutionEvent extends Event {
    private final Solution solution;
    private final SolutionDetails details;

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

    @Override
    public final EventType getType() {
        return EventType.BEST_SOLUTION;
    }
}
