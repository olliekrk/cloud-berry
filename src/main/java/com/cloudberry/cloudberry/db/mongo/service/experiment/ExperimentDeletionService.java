package com.cloudberry.cloudberry.db.mongo.service.experiment;

import com.cloudberry.cloudberry.db.mongo.repository.ExperimentRepository;
import com.cloudberry.cloudberry.db.mongo.service.configuration.ExperimentConfigurationDeletionService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class ExperimentDeletionService {
    private final ExperimentRepository experimentRepository;

    private final ExperimentConfigurationDeletionService experimentConfigurationDeletionService;

    public Flux<Void> deleteExperimentById(ObjectId experimentId) {
        final Flux<ObjectId> fluxWithExperimentIds = createFluxWithExperimentRemoval(experimentId);
        return experimentConfigurationDeletionService.createFluxWithConfigurationRemoval(fluxWithExperimentIds);
    }

    /**
     * @return flux with ids of experiments
     */
    @NotNull
    private Flux<ObjectId> createFluxWithExperimentRemoval(ObjectId experimentId) {
        experimentRepository.deleteById(experimentId).block();
        return Flux.just(experimentId);
    }

}
