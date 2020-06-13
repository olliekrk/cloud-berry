package com.cloudberry.cloudberry.repository;

import com.cloudberry.cloudberry.db.mongo.data.logs.BestSolutionLog;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface BestSolutionLogsRepository extends ReactiveMongoRepository<BestSolutionLog, ObjectId> {
}
