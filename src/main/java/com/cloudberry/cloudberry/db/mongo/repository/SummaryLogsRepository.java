package com.cloudberry.cloudberry.db.mongo.repository;

import com.cloudberry.cloudberry.db.mongo.data.logs.MongoSummaryLog;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface SummaryLogsRepository extends ReactiveMongoRepository<MongoSummaryLog, ObjectId> {
}
