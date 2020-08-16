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

    public interface Fields {
        String OCCURRENCES_COUNT = "occurrencesCount";
        String STEP_NUMBER = "stepNumber";
        String WORKPLACE_ID = "workplaceId";
    }

    private final ObjectId computationId;
    private final Solution solution;
    private final SolutionDetails details;

    public BestSolutionEvent(ObjectId computationId, Solution solution, SolutionDetails details) {
        this.computationId = computationId;
        this.solution = solution;
        this.details = details;
    }

    @JsonCreator
    private BestSolutionEvent(@JsonProperty("computationId") ObjectId computationId,
                              @JsonProperty("time") Instant time,
                              @JsonProperty("solution") Solution solution,
                              @JsonProperty("details") SolutionDetails details) {
        super(time);
        this.computationId = computationId;
        this.solution = solution;
        this.details = details;
    }

}
