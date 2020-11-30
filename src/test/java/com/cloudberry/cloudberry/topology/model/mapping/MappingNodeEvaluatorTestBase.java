package com.cloudberry.cloudberry.topology.model.mapping;

import com.cloudberry.cloudberry.EmbeddedMongoTest;
import com.cloudberry.cloudberry.db.mongo.service.configuration.ExperimentConfigurationByDifferentIdsService;
import com.cloudberry.cloudberry.db.mongo.service.experiment.ExperimentByDifferentIdsService;
import com.cloudberry.cloudberry.topology.model.mapping.operations.ComputationMetaParameterExtractor;
import com.cloudberry.cloudberry.topology.model.mapping.operations.differentFieldsOperations.AddDifferentFields;
import com.cloudberry.cloudberry.topology.model.mapping.operations.differentFieldsOperations.DivideDifferentFields;
import com.cloudberry.cloudberry.topology.model.mapping.operations.differentFieldsOperations.EventExtractorUtils;
import com.cloudberry.cloudberry.topology.model.mapping.operations.differentFieldsOperations.MultiplyDifferentFields;
import com.cloudberry.cloudberry.topology.model.mapping.operations.differentFieldsOperations.SubtractDifferentFields;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

@EmbeddedMongoTest
@Import({MappingNodeEvaluator.class, AddDifferentFields.class, SubtractDifferentFields.class, MultiplyDifferentFields.class,
        DivideDifferentFields.class, EventExtractorUtils.class, ComputationMetaParameterExtractor.class, ExperimentByDifferentIdsService.class,
        ExperimentConfigurationByDifferentIdsService.class})
public abstract class MappingNodeEvaluatorTestBase {
    @Autowired
    protected MappingNodeEvaluator mappingNodeEvaluator;
}
