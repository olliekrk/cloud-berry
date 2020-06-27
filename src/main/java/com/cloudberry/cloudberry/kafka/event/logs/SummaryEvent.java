package com.cloudberry.cloudberry.kafka.event.logs;

import com.cloudberry.cloudberry.kafka.event.Event;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class SummaryEvent extends Event {
    private final UUID evaluationId;
    private final double bestEvaluation;
    private final long evaluationsCount;

    public SummaryEvent(UUID evaluationId, double bestEvaluation, long evaluationsCount) {
        this.evaluationId = evaluationId;
        this.bestEvaluation = bestEvaluation;
        this.evaluationsCount = evaluationsCount;
    }

    @JsonCreator
    private SummaryEvent(@JsonProperty("evaluationId") UUID evaluationId,
                         @JsonProperty("time") Instant time,
                         @JsonProperty("bestEvaluation") double bestEvaluation,
                         @JsonProperty("evaluationsCount") long evaluationsCount) {
        super(time);
        this.evaluationId = evaluationId;
        this.bestEvaluation = bestEvaluation;
        this.evaluationsCount = evaluationsCount;
    }

}
