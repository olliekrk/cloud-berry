package com.cloudberry.cloudberry.kafka.event.logs;

import com.cloudberry.cloudberry.kafka.event.Event;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.bson.types.ObjectId;

import java.time.Instant;

@Getter
public class SummaryEvent extends Event {

    public interface Fields {
        String BEST_COMPUTATION = "bestComputation";
        String COMPUTATIONS_COUNT = "computationsCount";
    }

    private final ObjectId computationId;
    private final double bestComputation;
    private final long computationsCount;

    public SummaryEvent(ObjectId computationId, double bestComputation, long computationsCount) {
        this.computationId = computationId;
        this.bestComputation = bestComputation;
        this.computationsCount = computationsCount;
    }

    @JsonCreator
    private SummaryEvent(@JsonProperty("computationId") ObjectId computationId,
                         @JsonProperty("time") Instant time,
                         @JsonProperty("bestComputation") double bestComputation,
                         @JsonProperty("computationsCount") long computationsCount) {
        super(time);
        this.computationId = computationId;
        this.bestComputation = bestComputation;
        this.computationsCount = computationsCount;
    }

}
