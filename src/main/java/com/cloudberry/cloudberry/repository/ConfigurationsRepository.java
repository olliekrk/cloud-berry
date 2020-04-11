package com.cloudberry.cloudberry.repository;

import com.cloudberry.cloudberry.model.metadata.ExperimentConfiguration;
import org.bson.types.ObjectId;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ConfigurationsRepository extends ReactiveSortingRepository<ExperimentConfiguration, ObjectId> {
    Flux<ExperimentConfiguration> findAllByExperimentId(ObjectId experimentId);
}
