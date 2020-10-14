package com.cloudberry.cloudberry.service.deletion;

import com.cloudberry.cloudberry.analytics.model.InfluxQueryFields;
import com.cloudberry.cloudberry.db.mongo.service.computation.ComputationMetaDeletionService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExperimentComputationDeletionService {
    private final InfluxComputationDeletionService influxComputationDeletionService;

    private final ComputationMetaDeletionService computationMetaDeletionService;

    public void delete(List<ObjectId> computationIds, InfluxQueryFields influxQueryFields) {
        influxComputationDeletionService.deleteByComputationId(influxQueryFields, computationIds);

        computationMetaDeletionService.deleteComputationById(computationIds).blockLast();
    }
}
