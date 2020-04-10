package com.cloudberry.cloudberry.model.logs;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.index.Indexed;

/**
 * BH from AgE
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BestSolutionLog extends Log {
    //SOLUTION_STRING;SOLUTION_WORKPLACE_ID;SOLUTION_WORKPLACE_STEP_NUMBER;SOLUTION_OCCURRENCE_COUNT
    private String solutionString;
    @Indexed
    private ObjectId workplaceId;
    private long solutionWorkplaceStepNumber;
    private long solutionOccurrenceCount;
}
