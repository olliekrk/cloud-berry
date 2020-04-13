package com.cloudberry.cloudberry.model.logs;

import com.cloudberry.cloudberry.model.event.BestSolutionEvent;
import com.cloudberry.cloudberry.model.event.EventType;
import com.cloudberry.cloudberry.model.solution.Solution;
import com.cloudberry.cloudberry.model.solution.SolutionDetails;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.UUID;

/**
 * B from AgE
 */
@EqualsAndHashCode(callSuper = true)
@Value
@Document(collection = "best_solution_log")
public class BestSolutionLog extends Log {
    Solution solution;
    @Indexed
    long workplaceId;
    long stepNumber;
    long occurrencesCount;

    public BestSolutionLog(Instant time,
                           UUID evaluationId,
                           Solution solution,
                           SolutionDetails details) {
        super(time, evaluationId);
        this.solution = solution;
        this.workplaceId = details.workplaceId;
        this.stepNumber = details.stepNumber;
        this.occurrencesCount = details.occurrencesCount;
    }

    @Override
    public final EventType getType() {
        return EventType.BEST_SOLUTION;
    }

    public static BestSolutionLog ofEvent(BestSolutionEvent event) {
        return new BestSolutionLog(event.getTime(), event.getEvaluationId(), event.getSolution(), event.getDetails());
    }
}
