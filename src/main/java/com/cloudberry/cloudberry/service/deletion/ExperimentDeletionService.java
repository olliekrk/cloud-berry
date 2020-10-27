package com.cloudberry.cloudberry.service.deletion;

import com.cloudberry.cloudberry.analytics.model.query.InfluxQueryFields;
import com.cloudberry.cloudberry.db.mongo.data.metadata.ExperimentComputation;
import com.cloudberry.cloudberry.db.mongo.service.computation.ComputationsByDifferentIdsService;
import com.cloudberry.cloudberry.db.mongo.service.experiment.ExperimentMetaDeletionService;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExperimentDeletionService {
    private final InfluxComputationDeletionService influxComputationDeletionService;

    private final ExperimentMetaDeletionService experimentMetaDeletionService;
    private final ComputationsByDifferentIdsService computationsByDifferentIdsService;

    public void delete(List<ObjectId> experimentIds, InfluxQueryFields influxQueryFields) {
        val computationIds = Flux.fromIterable(experimentIds)
                .flatMap(computationsByDifferentIdsService::findByExperimentId)
                .map(ExperimentComputation::getId)
                .collectList().block();

        influxComputationDeletionService.deleteByComputationId(influxQueryFields, computationIds);

        experimentMetaDeletionService.deleteExperimentById(experimentIds).blockLast();
    }

}
