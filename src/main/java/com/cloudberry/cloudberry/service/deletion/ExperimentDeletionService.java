package com.cloudberry.cloudberry.service.deletion;

import com.cloudberry.cloudberry.analytics.model.InfluxQueryFields;
import com.cloudberry.cloudberry.db.mongo.data.metadata.ExperimentComputation;
import com.cloudberry.cloudberry.db.mongo.data.metadata.ExperimentConfiguration;
import com.cloudberry.cloudberry.db.mongo.repository.ConfigurationRepository;
import com.cloudberry.cloudberry.db.mongo.service.computation.ExperimentComputationCRUService;
import com.cloudberry.cloudberry.db.mongo.service.experiment.ExperimentMetaDeletionService;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExperimentDeletionService {
    private final InfluxComputationDeletionService influxComputationDeletionService;

    private final ExperimentMetaDeletionService experimentMetaDeletionService;
    private final ConfigurationRepository configurationRepository;

    private final ExperimentComputationCRUService experimentComputationCRUService;

    public void delete(List<ObjectId> experimentIds, InfluxQueryFields influxQueryFields) {
        val computationIds = experimentIds.stream()
                .map(exp -> configurationRepository.findAllByExperimentId(exp).collectList().block())
                .flatMap(List::stream)
                .map(ExperimentConfiguration::getId)
                .map(experimentComputationCRUService::findAllComputationsForConfigurationId)
                .flatMap(List::stream)
                .map(ExperimentComputation::getId)
                .collect(Collectors.toList());

        influxComputationDeletionService.deleteByComputationId(influxQueryFields, computationIds);

        experimentMetaDeletionService.deleteExperimentById(experimentIds).blockLast();
    }

}
