package com.cloudberry.cloudberry.db.mongo.repository;

import com.cloudberry.cloudberry.db.mongo.data.metadata.ExperimentComputation;
import org.bson.types.ObjectId;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import reactor.core.publisher.Flux;

import java.util.Collection;

public interface ComputationsRepository extends ReactiveSortingRepository<ExperimentComputation, ObjectId> {

    Flux<ExperimentComputation> findAllByConfigurationId(ObjectId configurationId);

}
