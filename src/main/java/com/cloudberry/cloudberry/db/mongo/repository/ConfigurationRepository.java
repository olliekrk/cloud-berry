package com.cloudberry.cloudberry.db.mongo.repository;

import com.cloudberry.cloudberry.db.mongo.data.metadata.ExperimentConfiguration;
import org.bson.types.ObjectId;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import reactor.core.publisher.Flux;

import java.util.Map;

public interface ConfigurationRepository extends ReactiveSortingRepository<ExperimentConfiguration, ObjectId> {
    Flux<ExperimentConfiguration> findAllByExperimentId(ObjectId experimentId);

    Flux<ExperimentConfiguration> findAllByExperimentIdAndParameters(
            ObjectId experimentId,
            Map<String, Object> parameters
    );

    Flux<ExperimentConfiguration> findAllByConfigurationFileName(String configurationFileName);

    Flux<Void> deleteByExperimentId(ObjectId experimentId);

}
