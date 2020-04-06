package com.cloudberry.cloudberry.model.event;

public class SummaryEvent extends TimedEvent {
    public final Double bestEvaluation;
    public final Long evaluationsCount;

    public SummaryEvent(Double bestEvaluation, Long evaluationsCount) {
        this.bestEvaluation = bestEvaluation;
        this.evaluationsCount = evaluationsCount;
    }
}
