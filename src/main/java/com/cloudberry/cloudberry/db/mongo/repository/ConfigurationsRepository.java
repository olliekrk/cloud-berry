package com.cloudberry.cloudberry.db.mongo.repository;

import com.cloudberry.cloudberry.db.mongo.data.metadata.ExperimentConfiguration;
import org.bson.types.ObjectId;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import reactor.core.publisher.Flux;

public interface ConfigurationsRepository extends ReactiveSortingRepository<ExperimentConfiguration, ObjectId> {
    Flux<ExperimentConfiguration> findAllByExperimentId(Long experimentId);
}
