package com.cloudberry.cloudberry.db.mongo.service.computation;

import com.cloudberry.cloudberry.db.mongo.data.metadata.ExperimentComputation;
import com.cloudberry.cloudberry.db.mongo.repository.ComputationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExperimentComputationCRUService {
    private final ComputationRepository computationRepository;

    public Flux<ExperimentComputation> findAll() {
        return computationRepository.findAll();
    }

    public Mono<ExperimentComputation> findOrCreateComputation(ExperimentComputation computation) {
        return findExistingComputation(computation)
                .switchIfEmpty(saveNewComputation(computation));
    }

    private Mono<ExperimentComputation> saveNewComputation(
            ExperimentComputation computation
    ) {
        return computationRepository.save(computation)
                .doOnNext(_e -> log.info("Created new computation " + computation.getId()));
    }

    private Mono<ExperimentComputation> findExistingComputation(
            ExperimentComputation computation
    ) {
        return computationRepository.findById(computation.getId())
                .doOnNext(existing -> log.info("Existing computation " + existing.getId() + " was found"));
    }

}
