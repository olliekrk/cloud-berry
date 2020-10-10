package com.cloudberry.cloudberry.db.mongo.service.computation.delete;

import com.cloudberry.cloudberry.db.mongo.service.computation.ComputationDeletionService;
import com.cloudberry.cloudberry.db.mongo.service.computation.ExperimentComputationServiceTestBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

@Import({ComputationDeletionService.class})
public class ExperimentComputationDeletionServiceTestBase extends ExperimentComputationServiceTestBase {
    @Autowired
    protected ComputationDeletionService computationDeletionService;
}
