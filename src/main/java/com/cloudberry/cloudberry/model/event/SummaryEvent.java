package com.cloudberry.cloudberry.model.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.UUID;

public class SummaryEvent extends Event {
    public final double bestEvaluation;
    public final long evaluationsCount;

    public SummaryEvent(UUID evaluationId, double bestEvaluation, long evaluationsCount) {
        super(evaluationId);
        this.bestEvaluation = bestEvaluation;
        this.evaluationsCount = evaluationsCount;
    }

    @JsonCreator
    private SummaryEvent(@JsonProperty("evaluationId") UUID evaluationId,
                         @JsonProperty("time") Instant time,
                         @JsonProperty("bestEvaluation") double bestEvaluation,
                         @JsonProperty("evaluationsCount") long evaluationsCount) {
        super(evaluationId, time);
        this.bestEvaluation = bestEvaluation;
        this.evaluationsCount = evaluationsCount;
    }
}
