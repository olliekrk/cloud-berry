package com.cloudberry.cloudberry.repository.facades;

import com.cloudberry.cloudberry.model.event.ProblemDefinitionEvent;
import reactor.core.publisher.Mono;

public interface MetadataSaver {
    Mono<Void> saveMetadata(ProblemDefinitionEvent event);
}
