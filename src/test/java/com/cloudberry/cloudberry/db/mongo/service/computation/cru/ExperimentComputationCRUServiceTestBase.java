package com.cloudberry.cloudberry.db.mongo.service.computation.cru;

import com.cloudberry.cloudberry.db.mongo.service.computation.ExperimentComputationCRUService;
import com.cloudberry.cloudberry.db.mongo.service.computation.ExperimentComputationServiceTestBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

@Import({ExperimentComputationCRUService.class})
public class ExperimentComputationCRUServiceTestBase extends ExperimentComputationServiceTestBase {

    @Autowired
    protected ExperimentComputationCRUService experimentComputationCRUService;
}
