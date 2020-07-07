package com.cloudberry.cloudberry.kafka.event.logs;

import com.cloudberry.cloudberry.kafka.event.Event;
import com.cloudberry.cloudberry.model.solution.Solution;
import com.cloudberry.cloudberry.model.solution.SolutionDetails;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.bson.types.ObjectId;

import java.time.Instant;

@Getter
public class BestSolutionEvent extends Event {
    private final ObjectId evaluationId;
    private final Solution solution;
    private final SolutionDetails details;

    public BestSolutionEvent(ObjectId evaluationId, Solution solution, SolutionDetails details) {
        this.evaluationId = evaluationId;
        this.solution = solution;
        this.details = details;
    }

    @JsonCreator
    private BestSolutionEvent(@JsonProperty("evaluationId") ObjectId evaluationId,
                              @JsonProperty("time") Instant time,
                              @JsonProperty("solution") Solution solution,
                              @JsonProperty("details") SolutionDetails details) {
        super(time);
        this.evaluationId = evaluationId;
        this.solution = solution;
        this.details = details;
    }

}
