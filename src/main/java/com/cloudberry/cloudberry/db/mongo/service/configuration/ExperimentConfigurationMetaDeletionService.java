package com.cloudberry.cloudberry.db.mongo.service.configuration;

import com.cloudberry.cloudberry.db.mongo.data.metadata.ExperimentConfiguration;
import com.cloudberry.cloudberry.db.mongo.repository.ConfigurationRepository;
import com.cloudberry.cloudberry.db.mongo.service.computation.ComputationMetaDeletionService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExperimentConfigurationMetaDeletionService {
    private final ConfigurationRepository configurationRepository;

    private final ComputationMetaDeletionService computationMetaDeletionService;

    public Flux<Void> deleteConfigurationById(List<ObjectId> configurationIds) {
        final Flux<ObjectId> fluxWithConfigurationsIds = Flux.fromIterable(configurationIds);
        return createFluxWithConfigurationRemovalByConfigurationIds(fluxWithConfigurationsIds);
    }

    /**
     * @return flux with ids of configuration
     */
    @NotNull
    public Flux<Void> createFluxWithConfigurationRemoval(Flux<ObjectId> fluxWithExperimentIds) {

        //its deleted in next line so we save it earlier
        final Flux<ObjectId> fluxWithConfigurationsIds =
                Flux.fromIterable(
                        fluxWithExperimentIds.flatMap(configurationRepository::findAllByExperimentId)
                                .map(ExperimentConfiguration::getId)
                                .collectList()
                                .block()
                );

        fluxWithExperimentIds.flatMap(configurationRepository::deleteByExperimentId).blockLast();
        return computationRemoval(fluxWithConfigurationsIds);
    }

    private Flux<Void> createFluxWithConfigurationRemovalByConfigurationIds(Flux<ObjectId> fluxWithConfigurationsIds) {
        fluxWithConfigurationsIds.flatMap(configurationRepository::deleteById).blockLast();
        return computationRemoval(fluxWithConfigurationsIds);
    }

    @NotNull
    private Flux<Void> computationRemoval(Flux<ObjectId> fluxWithConfigurationsIds) {
        return computationMetaDeletionService.createFluxWithComputationRemovalByConfigurationIds(fluxWithConfigurationsIds);
    }
}
