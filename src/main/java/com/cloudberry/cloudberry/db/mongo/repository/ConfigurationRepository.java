package com.cloudberry.cloudberry.db.mongo.repository;

import com.cloudberry.cloudberry.db.mongo.data.metadata.ExperimentConfiguration;
import org.bson.types.ObjectId;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import reactor.core.publisher.Flux;

public interface ConfigurationRepository extends ReactiveSortingRepository<ExperimentConfiguration, ObjectId> {
    Flux<ExperimentConfiguration> findAllByExperimentId(ObjectId experimentId);

    Flux<ExperimentConfiguration> findAllByConfigurationFileName(String configurationFileName);

    Flux<Void> deleteByExperimentId(ObjectId experimentId);

}
