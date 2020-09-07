package com.cloudberry.cloudberry.db.mongo.service;

import com.cloudberry.cloudberry.db.mongo.data.metadata.ExperimentComputation;
import com.cloudberry.cloudberry.db.mongo.repository.ComputationsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ComputationService {
    private final ComputationsRepository computationsRepository;

    public List<ExperimentComputation> findAll() {
        return computationsRepository.findAll().collectList().block();
    }

    public List<ExperimentComputation> findAllComputationsForConfigurationId(ObjectId configurationId) {
        return computationsRepository.findAllByConfigurationId(configurationId)
                .collectList()
                .block();
    }

    public Mono<ExperimentComputation> getOrCreateComputation(ExperimentComputation computation) {
        return computationsRepository
                .findById(computation.getId())
                .doOnNext(next -> log.info("Existing computation " + next.getId() + " was found"))
                .switchIfEmpty(computationsRepository.save(computation))
                .doOnNext(next -> log.info("Created new computation " + next.getId()));
    }
}
