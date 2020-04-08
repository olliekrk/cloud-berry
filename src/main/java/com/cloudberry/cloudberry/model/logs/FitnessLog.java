package com.cloudberry.cloudberry.model.logs;

import java.time.Instant;

public class FitnessLog extends Log {

    private final double fitness;

    public FitnessLog(Instant eventTime, double fitness) {
        super(eventTime);
        this.fitness = fitness;
    }

    @Override
    public String toString() {
        return "FitnessLog{" +
                "fitness=" + fitness +
                ", id='" + id + '\'' +
                ", eventTime=" + eventTime +
                '}';
    }
}
