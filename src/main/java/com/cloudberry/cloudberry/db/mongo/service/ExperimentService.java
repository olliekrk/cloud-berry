package com.cloudberry.cloudberry.db.mongo.service;

import com.cloudberry.cloudberry.db.mongo.data.metadata.Experiment;
import com.cloudberry.cloudberry.db.mongo.repository.ExperimentsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExperimentService {
    private final ExperimentsRepository experimentsRepository;

    public List<Experiment> findAll() {
        return experimentsRepository.findAll().collectList().block();
    }

    public List<Experiment> findByName(String name) {
        return experimentsRepository.findAllByName(name).collectList().block();
    }

    public Mono<Experiment> getOrCreateExperiment(Experiment experiment) {
        return experimentsRepository
                .findById(experiment.getId())
                .switchIfEmpty(
                        experimentsRepository.findAllByName(experiment.getName())
                                .filter(existing -> existing.getParameters().equals(experiment.getParameters()))
                                .limitRequest(1)
                                .next()
                )
                .doOnNext(next -> log.info("Existing experiment " + next.getId() + " was found"))
                .switchIfEmpty(experimentsRepository.save(experiment))
                .doOnNext(next -> log.info("Created new experiment " + next.getId()));
    }
}
