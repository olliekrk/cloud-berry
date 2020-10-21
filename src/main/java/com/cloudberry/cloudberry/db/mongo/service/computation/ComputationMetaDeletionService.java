package com.cloudberry.cloudberry.db.mongo.service.computation;

import com.cloudberry.cloudberry.db.mongo.repository.ComputationRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ComputationMetaDeletionService {
    private final ComputationRepository computationRepository;

    public Flux<Void> deleteComputationById(List<ObjectId> computationId) {
        final Flux<ObjectId> fluxWithComputationIds = Flux.fromIterable(computationId);
        return createFluxWithComputationRemovalByComputationIds(fluxWithComputationIds);
    }

    @NotNull
    public Flux<Void> createFluxWithComputationRemovalByConfigurationIds(
            @NotNull Flux<ObjectId> fluxWithConfigurationIds
    ) {
        return fluxWithConfigurationIds
                .flatMap(computationRepository::deleteByConfigurationId);
    }

    @NotNull
    private Flux<Void> createFluxWithComputationRemovalByComputationIds(Flux<ObjectId> fluxWithConfigurationIds) {
        return fluxWithConfigurationIds
                .flatMap(computationRepository::deleteById);
    }

}
