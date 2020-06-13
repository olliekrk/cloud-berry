package com.cloudberry.cloudberry.repository;

import com.cloudberry.cloudberry.db.mongo.data.logs.WorkplaceLog;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface WorkplaceLogsRepository extends ReactiveMongoRepository<WorkplaceLog, ObjectId> {
}
