package com.cloudberry.cloudberry;


import com.cloudberry.cloudberry.model.logs.FitnessLog;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class Test1 {

    @Test
    public void test()
    {
        FitnessLog fitnessLog = new FitnessLog(Instant.EPOCH, 2.3);
        assertTrue(true);
    }
}
