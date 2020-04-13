package com.cloudberry.cloudberry.repository;

import com.cloudberry.cloudberry.model.metadata.Experiment;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

public interface ExperimentsRepository extends ReactiveSortingRepository<Experiment, Long> {
    Flux<Experiment> findAllByName(String name);
}
