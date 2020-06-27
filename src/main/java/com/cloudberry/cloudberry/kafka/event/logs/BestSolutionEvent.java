package com.cloudberry.cloudberry.kafka.event.logs;

import com.cloudberry.cloudberry.kafka.event.Event;
import com.cloudberry.cloudberry.model.solution.Solution;
import com.cloudberry.cloudberry.model.solution.SolutionDetails;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class BestSolutionEvent extends Event {
    private final UUID evaluationId;
    private final Solution solution;
    private final SolutionDetails details;

    public BestSolutionEvent(UUID evaluationId, Solution solution, SolutionDetails details) {
        this.evaluationId = evaluationId;
        this.solution = solution;
        this.details = details;
    }

    @JsonCreator
    private BestSolutionEvent(@JsonProperty("evaluationId") UUID evaluationId,
                              @JsonProperty("time") Instant time,
                              @JsonProperty("solution") Solution solution,
                              @JsonProperty("details") SolutionDetails details) {
        super(time);
        this.evaluationId = evaluationId;
        this.solution = solution;
        this.details = details;
    }

}
