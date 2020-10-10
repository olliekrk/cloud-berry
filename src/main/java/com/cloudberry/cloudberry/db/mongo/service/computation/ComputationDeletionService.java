package com.cloudberry.cloudberry.db.mongo.service.computation;

import com.cloudberry.cloudberry.db.mongo.repository.ComputationRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class ComputationDeletionService {
    private final ComputationRepository computationRepository;

    public Flux<Void> deleteComputationById(ObjectId computationId) {
        final Flux<ObjectId> fluxWithComputationIds = Flux.just(computationId);
        return createFluxWithComputationRemovalByComputationIds(fluxWithComputationIds);
    }

    @NotNull
    public Flux<Void> createFluxWithComputationRemovalByConfigurationIds(
            @NotNull Flux<ObjectId> fluxWithConfigurationIds) {
        return fluxWithConfigurationIds
                .flatMap(computationRepository::deleteByConfigurationId);
    }

    @NotNull
    Flux<Void> createFluxWithComputationRemovalByComputationIds(@NotNull Flux<ObjectId> fluxWithConfigurationIds) {
        return fluxWithConfigurationIds
                .flatMap(computationRepository::deleteById);
    }

}
