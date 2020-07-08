package com.cloudberry.cloudberry.db.mongo.repository;

import com.cloudberry.cloudberry.db.mongo.data.metadata.Experiment;
import org.bson.types.ObjectId;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ExperimentsRepository extends ReactiveSortingRepository<Experiment, ObjectId> {

    Flux<Experiment> findAllByName(String name);

    Mono<Experiment> findByName(String name);
}
