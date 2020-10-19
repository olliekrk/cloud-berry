package com.cloudberry.cloudberry.db.mongo.service.experiment;

import com.cloudberry.cloudberry.db.mongo.repository.ExperimentRepository;
import com.cloudberry.cloudberry.db.mongo.service.configuration.ExperimentConfigurationMetaDeletionService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExperimentMetaDeletionService {
    private final ExperimentRepository experimentRepository;

    private final ExperimentConfigurationMetaDeletionService experimentConfigurationMetaDeletionService;

    public Flux<Void> deleteExperimentById(List<ObjectId> experimentIds) {
        final Flux<ObjectId> fluxWithExperimentIds = Flux.fromIterable(experimentIds);
        final Flux<ObjectId> fluxWithExperimentIdsRemoval = createFluxWithExperimentRemoval(fluxWithExperimentIds);
        return experimentConfigurationMetaDeletionService.createFluxWithConfigurationRemoval(fluxWithExperimentIdsRemoval);
    }

    /**
     * @return flux with ids of experiments
     */
    @NotNull
    private Flux<ObjectId> createFluxWithExperimentRemoval(Flux<ObjectId> experimentIds) {
        experimentIds.flatMap(experimentRepository::deleteById).blockLast();
        return experimentIds;
    }

}
