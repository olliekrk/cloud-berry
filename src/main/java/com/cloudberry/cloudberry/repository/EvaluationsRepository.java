package com.cloudberry.cloudberry.repository;

import com.cloudberry.cloudberry.model.metadata.ExperimentEvaluation;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

public interface EvaluationsRepository extends ReactiveSortingRepository<ExperimentEvaluation, UUID> {
}
