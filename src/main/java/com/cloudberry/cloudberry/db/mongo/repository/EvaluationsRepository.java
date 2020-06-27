package com.cloudberry.cloudberry.db.mongo.repository;

import com.cloudberry.cloudberry.db.mongo.data.metadata.ExperimentEvaluation;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;

import java.util.UUID;

public interface EvaluationsRepository extends ReactiveSortingRepository<ExperimentEvaluation, UUID> {
}
