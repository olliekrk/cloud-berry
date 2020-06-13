package com.cloudberry.cloudberry.repository;

import com.cloudberry.cloudberry.db.mongo.data.logs.SummaryLog;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface SummaryLogsRepository extends ReactiveMongoRepository<SummaryLog, ObjectId> {
}
