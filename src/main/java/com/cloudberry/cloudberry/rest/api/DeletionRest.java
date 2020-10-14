package com.cloudberry.cloudberry.rest.api;

import com.cloudberry.cloudberry.rest.exceptions.invalid.id.InvalidComputationIdException;
import com.cloudberry.cloudberry.rest.exceptions.invalid.id.InvalidConfigurationIdException;
import com.cloudberry.cloudberry.rest.exceptions.invalid.id.InvalidExperimentIdException;
import com.cloudberry.cloudberry.rest.util.IdDispatcher;
import com.cloudberry.cloudberry.service.deletion.ExperimentComputationDeletionService;
import com.cloudberry.cloudberry.service.deletion.ExperimentConfigurationDeletionService;
import com.cloudberry.cloudberry.service.deletion.ExperimentDeletionService;
import com.cloudberry.cloudberry.service.utility.InfluxQueryFieldsResolver;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/delete", consumes = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class DeletionRest {

    private final ExperimentComputationDeletionService experimentComputationDeletionService;
    private final ExperimentConfigurationDeletionService experimentConfigurationDeletionService;
    private final ExperimentDeletionService experimentDeletionService;

    private final InfluxQueryFieldsResolver influxQueryFieldsResolver;

    @DeleteMapping("/computation")
    void deleteComputations(@RequestParam List<String> computationIdsHex,
                            @RequestParam(required = false) String bucketName,
                            @RequestParam(required = false) String measurementName)
            throws InvalidComputationIdException {
        val computationIds = IdDispatcher.getComputationIds(computationIdsHex);

        val influxQueryFields = influxQueryFieldsResolver.get(measurementName, bucketName);
        experimentComputationDeletionService.delete(computationIds, influxQueryFields);
    }

    @DeleteMapping("/configuration")
    void deleteConfigurations(@RequestParam List<String> configurationIdsHex,
                              @RequestParam(required = false) String bucketName,
                              @RequestParam(required = false) String measurementName)
            throws InvalidConfigurationIdException {
        val configurationIds = IdDispatcher.getConfigurationIds(configurationIdsHex);

        val influxQueryFields = influxQueryFieldsResolver.get(measurementName, bucketName);
        experimentConfigurationDeletionService.delete(configurationIds, influxQueryFields);
    }

    @DeleteMapping("/experiment")
    void deleteExperiments(@RequestParam List<String> experimentIdsHex,
                           @RequestParam(required = false) String bucketName,
                           @RequestParam(required = false) String measurementName) throws InvalidExperimentIdException {
        val experimentIds = IdDispatcher.getExperimentIds(experimentIdsHex);

        val influxQueryFields = influxQueryFieldsResolver.get(measurementName, bucketName);
        experimentDeletionService.delete(experimentIds, influxQueryFields);
    }

}
