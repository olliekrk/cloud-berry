package com.cloudberry.cloudberry.rest.util;

import com.cloudberry.cloudberry.rest.exceptions.invalid.id.InvalidComputationIdException;
import com.cloudberry.cloudberry.rest.exceptions.invalid.id.InvalidConfigurationIdException;
import com.cloudberry.cloudberry.rest.exceptions.invalid.id.InvalidExperimentIdException;
import org.bson.types.ObjectId;

import java.util.List;

public final class IdDispatcher {

    public static ObjectId getConfigurationId(String configurationIdHex)
            throws InvalidConfigurationIdException {
        return RestParametersUtil.getValidId(configurationIdHex)
                .orElseThrow(() -> new InvalidConfigurationIdException(List.of(configurationIdHex)));
    }

    public static List<ObjectId> getConfigurationIds(List<String> configurationIdsHex)
            throws InvalidConfigurationIdException {
        var configurationIds = RestParametersUtil.getValidIds(configurationIdsHex);
        if (configurationIds.isEmpty()) { throw new InvalidConfigurationIdException(configurationIdsHex); }
        return configurationIds;
    }

    public static ObjectId getExperimentId(String experimentIdHex)
            throws InvalidExperimentIdException {
        return RestParametersUtil.getValidId(experimentIdHex)
                .orElseThrow(() -> new InvalidExperimentIdException(List.of(experimentIdHex)));
    }

    public static List<ObjectId> getExperimentIds(List<String> experimentsIdHex) throws InvalidExperimentIdException {
        var experimentIds = RestParametersUtil.getValidIds(experimentsIdHex);
        if (experimentIds.isEmpty()) { throw new InvalidExperimentIdException(experimentsIdHex); }
        return experimentIds;
    }

    public static ObjectId getComputationId(String computationIdHex)
            throws InvalidComputationIdException {
        return RestParametersUtil.getValidId(computationIdHex)
                .orElseThrow(() -> new InvalidComputationIdException(List.of(computationIdHex)));
    }

    public static List<ObjectId> getComputationIds(List<String> computationIdsHex)
            throws InvalidComputationIdException {
        var computationIds = RestParametersUtil.getValidIds(computationIdsHex);
        if (computationIds.isEmpty()) { throw new InvalidComputationIdException(computationIdsHex); }
        return computationIds;
    }
}
