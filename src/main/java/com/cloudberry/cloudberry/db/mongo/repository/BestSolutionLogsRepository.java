package com.cloudberry.cloudberry.db.mongo.repository;

import com.cloudberry.cloudberry.db.mongo.data.logs.MongoBestSolutionLog;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface BestSolutionLogsRepository extends ReactiveMongoRepository<MongoBestSolutionLog, ObjectId> {
}
