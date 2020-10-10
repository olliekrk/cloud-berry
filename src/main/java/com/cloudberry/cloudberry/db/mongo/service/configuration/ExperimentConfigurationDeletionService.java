package com.cloudberry.cloudberry.db.mongo.service.configuration;

import com.cloudberry.cloudberry.db.mongo.data.metadata.ExperimentConfiguration;
import com.cloudberry.cloudberry.db.mongo.repository.ConfigurationRepository;
import com.cloudberry.cloudberry.db.mongo.service.computation.ComputationDeletionService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class ExperimentConfigurationDeletionService {
    private final ConfigurationRepository configurationRepository;

    private final ComputationDeletionService computationDeletionService;

    public Flux<Void> deleteConfigurationById(ObjectId configurationId) {
        final Flux<ObjectId> fluxWithExperimentIds = Flux.just(configurationId);
        return createFluxWithConfigurationRemoval(fluxWithExperimentIds);
    }

    /**
     * @return flux with ids of configuration
     */
    @NotNull
    public Flux<Void> createFluxWithConfigurationRemoval(Flux<ObjectId> fluxWithExperimentIds) {

        //its deleted in next line so we save it earlier
        final Flux<ObjectId> fluxWithConfigurationsIds =
                Flux.fromIterable(fluxWithExperimentIds.flatMap(configurationRepository::findAllByExperimentId).map(
                        ExperimentConfiguration::getId).collectList().block());

        fluxWithExperimentIds.flatMap(experimentId -> configurationRepository.deleteByExperimentId(experimentId))
                .blockLast();

        return computationDeletionService.createFluxWithComputationRemovalByConfigurationIds(fluxWithConfigurationsIds);
    }
}
