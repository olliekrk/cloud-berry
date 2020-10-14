package com.cloudberry.cloudberry.service.deletion;

import com.cloudberry.cloudberry.analytics.model.InfluxQueryFields;
import com.cloudberry.cloudberry.db.mongo.data.metadata.ExperimentComputation;
import com.cloudberry.cloudberry.db.mongo.service.computation.ComputationsByDifferentIdsService;
import com.cloudberry.cloudberry.db.mongo.service.configuration.ExperimentConfigurationMetaDeletionService;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExperimentConfigurationDeletionService {
    private final InfluxComputationDeletionService influxComputationDeletionService;
    private final ExperimentConfigurationMetaDeletionService experimentConfigurationMetaDeletionService;

    private final ComputationsByDifferentIdsService computationsByDifferentIdsService;

    public void delete(List<ObjectId> configurationIds, InfluxQueryFields influxQueryFields) {
        val computationIds = Flux.fromIterable(configurationIds)
                .flatMap(computationsByDifferentIdsService::findByConfigurationId)
                .map(ExperimentComputation::getId)
                .collectList()
                .block();

        influxComputationDeletionService.deleteByComputationId(influxQueryFields, computationIds);

        experimentConfigurationMetaDeletionService.deleteConfigurationById(configurationIds).blockLast();
    }
}
