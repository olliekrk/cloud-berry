package com.cloudberry.cloudberry.db.mongo.service.computation.delete;

import com.cloudberry.cloudberry.db.mongo.service.computation.ComputationMetaDeletionService;
import com.cloudberry.cloudberry.db.mongo.service.computation.ExperimentComputationServiceTestBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

@Import({ComputationMetaDeletionService.class})
public class ExperimentComputationDeletionServiceTestBase extends ExperimentComputationServiceTestBase {
    @Autowired
    protected ComputationMetaDeletionService computationMetaDeletionService;
}
