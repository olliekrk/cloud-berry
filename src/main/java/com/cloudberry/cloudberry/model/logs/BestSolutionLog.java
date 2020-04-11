package com.cloudberry.cloudberry.model.logs;

import com.cloudberry.cloudberry.model.solution.Solution;
import com.cloudberry.cloudberry.model.solution.SolutionDetails;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * B from AgE
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Document(collection = "best_solution_log")
public class BestSolutionLog extends Log {
    private Solution solution;
    @Indexed
    private long workplaceId;
    private long stepNumber;
    private long occurrencesCount;
}
