package com.cloudberry.cloudberry.db.mongo.service;

import com.cloudberry.cloudberry.db.mongo.data.metadata.Experiment;
import com.cloudberry.cloudberry.db.mongo.data.metadata.ExperimentConfiguration;
import com.cloudberry.cloudberry.db.mongo.data.metadata.ExperimentEvaluation;
import com.cloudberry.cloudberry.db.mongo.repository.ConfigurationsRepository;
import com.cloudberry.cloudberry.db.mongo.repository.EvaluationsRepository;
import com.cloudberry.cloudberry.db.mongo.repository.ExperimentsRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MetadataService {
    private final ConfigurationsRepository configurationsRepository;
    private final EvaluationsRepository evaluationsRepository;
    private final ExperimentsRepository experimentsRepository;

    public List<ObjectId> findAllEvaluationIdsForConfiguration(ObjectId configurationId) {
        return evaluationsRepository.findAllByConfigurationId(configurationId)
                .map(ExperimentEvaluation::getId)
                .collectList()
                .block();
    }

    public List<ObjectId> findAllConfigurationIdsForExperiment(String experimentName) {
        return experimentsRepository.findByName(experimentName)
                .map(Experiment::getId)
                .flux()
                .flatMap(configurationsRepository::findAllByExperimentId)
                .map(ExperimentConfiguration::getId)
                .collectList()
                .block();
    }
}
