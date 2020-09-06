package com.cloudberry.cloudberry.db.mongo.repository;

import com.cloudberry.cloudberry.db.mongo.data.metadata.Experiment;
import org.bson.types.ObjectId;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import reactor.core.publisher.Flux;

import java.util.Map;

public interface ExperimentsRepository extends ReactiveSortingRepository<Experiment, ObjectId> {

    Flux<Experiment> findAllByName(String name);

    Flux<Experiment> findAllByNameAndParameters(String name, Map<String, Object> parameters);
}
