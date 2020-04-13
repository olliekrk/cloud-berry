package com.cloudberry.cloudberry.repository;

import com.cloudberry.cloudberry.model.logs.WorkplaceLog;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

public interface WorkplaceLogsRepository extends ReactiveMongoRepository<WorkplaceLog, ObjectId> {
}
