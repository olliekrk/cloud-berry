package com.cloudberry.cloudberry.topology.model.mapping.operations;

import com.cloudberry.cloudberry.db.mongo.service.configuration.ExperimentConfigurationByDifferentIdsService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor
public class ComputationMetaParameterExtractor {
    private final ExperimentConfigurationByDifferentIdsService experimentConfigurationByDifferentIdsService;

    public Object getValueFromMetaParametersMap(ObjectId computationId, String mapKey) {
        return experimentConfigurationByDifferentIdsService
                .findByComputationId(computationId)
                .blockOptional()
                .orElseThrow(() -> new IllegalArgumentException(
                        format("Configuration for computationId: %s not found", computationId)
                ))
                .getParameters()
                .get(mapKey);
    }

}
