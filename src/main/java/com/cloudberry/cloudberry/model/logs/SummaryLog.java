package com.cloudberry.cloudberry.model.logs;

import lombok.EqualsAndHashCode;
import lombok.Value;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.UUID;

/**
 * S tag from AgE
 */
@EqualsAndHashCode(callSuper = true)
@Value
@Document(collection = "summary_log")
public class SummaryLog extends Log {
    double bestEvaluation;
    long evaluationsCount;

    public SummaryLog(Instant time, UUID evaluationId, double bestEvaluation, long evaluationsCount) {
        super(time, evaluationId);
        this.bestEvaluation = bestEvaluation;
        this.evaluationsCount = evaluationsCount;
    }
}
