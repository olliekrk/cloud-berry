package com.cloudberry.cloudberry.repository;

import com.cloudberry.cloudberry.model.logs.SummaryLog;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SummaryLogsRepository extends ReactiveMongoRepository<SummaryLog, ObjectId> {
}
