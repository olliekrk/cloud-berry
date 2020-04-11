package com.cloudberry.cloudberry.model.event;

import java.util.UUID;

public class SummaryEvent extends Event {
    public final double bestEvaluation;
    public final long evaluationsCount;

    public SummaryEvent(UUID evaluationId, double bestEvaluation, long evaluationsCount) {
        super(evaluationId);
        this.bestEvaluation = bestEvaluation;
        this.evaluationsCount = evaluationsCount;
    }
}
