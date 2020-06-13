package com.cloudberry.cloudberry.repository;

import com.cloudberry.cloudberry.db.mongo.data.logs.MongoWorkplaceLog;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

import java.util.Collection;
import java.util.UUID;

public interface WorkplaceLogsRepository extends ReactiveMongoRepository<MongoWorkplaceLog, ObjectId> {

    Flux<MongoWorkplaceLog> findAllByEvaluationId(UUID evaluationId);

    Flux<MongoWorkplaceLog> findAllByEvaluationIdIn(Collection<UUID> evaluationId);

    Flux<MongoWorkplaceLog> findAllByEvaluationIdAndWorkplaceId(UUID evaluationId, long workplaceId);

}
