package com.cloudberry.cloudberry.repository;

import com.cloudberry.cloudberry.model.logs.Log;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

@Configuration
public interface LogsRepository extends ReactiveMongoRepository<Log, String> {
}
