package com.cloudberry.cloudberry.db.mongo.repository;

import com.cloudberry.cloudberry.db.mongo.data.metadata.Experiment;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import reactor.core.publisher.Flux;

public interface ExperimentsRepository extends ReactiveSortingRepository<Experiment, Long> {
    Flux<Experiment> findAllByName(String name);
}
