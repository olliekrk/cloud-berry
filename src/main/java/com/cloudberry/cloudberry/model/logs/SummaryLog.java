package com.cloudberry.cloudberry.model.logs;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * S tag from AgE
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Document(collection = "summary_log")
public class SummaryLog extends Log {
    public final double bestEvaluation;
    public final long evaluationsCount;

    public SummaryLog(double bestEvaluation, long evaluationsCount) {
        this.bestEvaluation = bestEvaluation;
        this.evaluationsCount = evaluationsCount;
    }
}
