package com.cloudberry.cloudberry.repository;

import com.cloudberry.cloudberry.model.metadata.Experiment;
import org.bson.types.ObjectId;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ExperimentsRepository extends ReactiveSortingRepository<Experiment, ObjectId> {
    Flux<Experiment> findAllByName(String name);
}
