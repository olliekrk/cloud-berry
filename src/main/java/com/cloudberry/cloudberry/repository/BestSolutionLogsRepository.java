package com.cloudberry.cloudberry.repository;

import com.cloudberry.cloudberry.model.logs.BestSolutionLog;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BestSolutionLogsRepository extends ReactiveMongoRepository<BestSolutionLog, ObjectId> {
}
