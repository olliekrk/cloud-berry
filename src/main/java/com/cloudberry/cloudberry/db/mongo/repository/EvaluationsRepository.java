package com.cloudberry.cloudberry.db.mongo.repository;

import com.cloudberry.cloudberry.db.mongo.data.metadata.ExperimentEvaluation;
import org.bson.types.ObjectId;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import reactor.core.publisher.Flux;

import java.util.Collection;

public interface EvaluationsRepository extends ReactiveSortingRepository<ExperimentEvaluation, ObjectId> {

    Flux<ExperimentEvaluation> findAllByConfigurationId(ObjectId configurationId);

    Flux<ExperimentEvaluation> findAllByConfigurationIdIn(Collection<ObjectId> configurationId);
}
