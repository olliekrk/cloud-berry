package com.cloudberry.cloudberry.model.logs;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * SH tag from AgE
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SummaryLog extends TimedLog {
    //TIME;BEST_SOLUTION_SO_FAR;FITNESS_EVALUATIONS
    private double bestEvaluationSoFar;
    private long evaluationCount;
}
