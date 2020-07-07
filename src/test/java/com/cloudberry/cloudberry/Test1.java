package com.cloudberry.cloudberry;


import com.cloudberry.cloudberry.db.mongo.data.logs.MongoWorkplaceLog;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class Test1 {

    @Test
    public void test() {
        MongoWorkplaceLog fitnessLog = new MongoWorkplaceLog(Instant.now(), ObjectId.get(), 1, Map.of());
        Map<String, Object> parameters = fitnessLog.getParameters();
        assertTrue(true);
    }
}
