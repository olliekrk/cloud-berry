package com.cloudberry.cloudberry.service.deletion;

import com.cloudberry.cloudberry.analytics.model.InfluxQueryFields;
import com.cloudberry.cloudberry.db.mongo.data.metadata.ExperimentComputation;
import com.cloudberry.cloudberry.db.mongo.service.computation.ExperimentComputationCRUService;
import com.cloudberry.cloudberry.db.mongo.service.configuration.ExperimentConfigurationMetaDeletionService;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExperimentConfigurationDeletionService {
    private final InfluxComputationDeletionService influxComputationDeletionService;

    private final ExperimentConfigurationMetaDeletionService experimentConfigurationMetaDeletionService;
    private final ExperimentComputationCRUService experimentComputationCRUService;

    public void delete(List<ObjectId> configurationIds, InfluxQueryFields influxQueryFields) {
        val computationIds = configurationIds.stream()
                .map(experimentComputationCRUService::findAllComputationsForConfigurationId)
                .flatMap(List::stream)
                .map(ExperimentComputation::getId)
                .collect(Collectors.toList());
        influxComputationDeletionService.deleteByComputationId(influxQueryFields, computationIds);

        experimentConfigurationMetaDeletionService.deleteConfigurationById(configurationIds).blockLast();
    }
}
