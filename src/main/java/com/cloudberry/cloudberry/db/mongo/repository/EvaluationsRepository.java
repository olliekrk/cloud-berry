package com.cloudberry.cloudberry.db.mongo.repository;

import com.cloudberry.cloudberry.db.mongo.data.metadata.ExperimentEvaluation;
import org.bson.types.ObjectId;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;

public interface EvaluationsRepository extends ReactiveSortingRepository<ExperimentEvaluation, ObjectId> {
}
