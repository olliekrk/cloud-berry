package com.cloudberry.cloudberry;


import com.cloudberry.cloudberry.db.mongo.data.logs.WorkplaceLog;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class Test1 {

    @Test
    public void test() {
        WorkplaceLog fitnessLog = new WorkplaceLog(Instant.now(), UUID.randomUUID(), 1, Map.of());
        Map<String, Object> parameters = fitnessLog.getParameters();
        assertTrue(true);
    }
}
