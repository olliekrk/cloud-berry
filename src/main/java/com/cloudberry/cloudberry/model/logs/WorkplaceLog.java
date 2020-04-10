package com.cloudberry.cloudberry.model.logs;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * WH log from AgE
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class WorkplaceLog extends TimedLog {
    //WORKPLACE_ID;STEP_NUMBER;POPULATION_SIZE;AVERAGE_FITNESS;ENERGY_SUM
    private long stepNumber;
    private int populationSize;
    private double averageFitness;
    private double energySum;
}
