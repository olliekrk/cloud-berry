package com.cloudberry.cloudberry.rest.api;

import com.cloudberry.cloudberry.analytics.model.CriteriaMode;
import com.cloudberry.cloudberry.analytics.model.dto.SeriesPack;
import com.cloudberry.cloudberry.analytics.model.optimization.Optimization;
import com.cloudberry.cloudberry.analytics.model.optimization.OptimizationGoal;
import com.cloudberry.cloudberry.analytics.model.optimization.OptimizationKind;
import com.cloudberry.cloudberry.analytics.model.thresholds.Thresholds;
import com.cloudberry.cloudberry.rest.exceptions.InvalidThresholdsException;
import com.cloudberry.cloudberry.rest.exceptions.invalid.id.InvalidConfigurationIdException;
import com.cloudberry.cloudberry.rest.util.IdDispatcher;
import com.cloudberry.cloudberry.service.api.ConfigurationSeriesService;
import com.cloudberry.cloudberry.service.utility.InfluxQueryFieldsResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("series/configurations")
public class ConfigurationSeriesRest {
    private final ConfigurationSeriesService configurationSeriesService;
    private final InfluxQueryFieldsResolver influxQueryFieldsResolver;

    @PostMapping("/comparison")
    public SeriesPack getByIds(
            @RequestParam String fieldName,
            @RequestParam(required = false) String measurementName,
            @RequestParam(required = false) String bucketName,
            @RequestBody List<String> configurationIdsHex
    ) throws InvalidConfigurationIdException {
        var configurationIds = IdDispatcher.getConfigurationIds(configurationIdsHex);
        return configurationSeriesService.getConfigurations(
                fieldName,
                influxQueryFieldsResolver.get(measurementName, bucketName),
                configurationIds
        );
    }

    @PostMapping("/comparisonForExperiment")
    public SeriesPack getByExperimentName(
            @RequestParam String fieldName,
            @RequestParam(required = false) String measurementName,
            @RequestParam(required = false) String bucketName,
            @RequestParam String experimentName
    ) {
        return configurationSeriesService.getConfigurationsForExperiment(
                fieldName,
                influxQueryFieldsResolver.get(measurementName, bucketName),
                experimentName
        );
    }

    @PostMapping("/best")
    public SeriesPack getBest(
            @RequestParam int n,
            @RequestParam String fieldName,
            @RequestParam OptimizationGoal optimizationGoal,
            @RequestParam OptimizationKind optimizationKind,
            @RequestParam(required = false) String measurementName,
            @RequestParam(required = false) String bucketName,
            @RequestBody List<String> configurationIdsHex
    ) throws InvalidConfigurationIdException {
        var configurationIds = IdDispatcher.getConfigurationIds(configurationIdsHex);
        return configurationSeriesService.getNBestConfigurations(
                n,
                fieldName,
                new Optimization(optimizationGoal, optimizationKind),
                influxQueryFieldsResolver.get(measurementName, bucketName),
                configurationIds
        );
    }

    @PostMapping("/bestForExperiment")
    public SeriesPack getBestForExperiment(
            @RequestParam int n,
            @RequestParam String fieldName,
            @RequestParam OptimizationGoal optimizationGoal,
            @RequestParam OptimizationKind optimizationKind,
            @RequestParam(required = false) String measurementName,
            @RequestParam(required = false) String bucketName,
            @RequestParam String experimentName
    ) {
        return configurationSeriesService.getNBestConfigurationsForExperiment(
                n,
                fieldName,
                new Optimization(optimizationGoal, optimizationKind),
                influxQueryFieldsResolver.get(measurementName, bucketName),
                experimentName
        );
    }

    @PostMapping("/exceedingThresholds")
    public SeriesPack getExceedingThresholds(
            @RequestParam String fieldName,
            @RequestParam CriteriaMode mode,
            @RequestParam(required = false) String measurementName,
            @RequestParam(required = false) String bucketName,
            @RequestPart(name = "thresholds") Thresholds thresholds,
            @RequestPart(name = "configurationIdsHex") List<String> configurationIdsHex
    ) throws InvalidThresholdsException, InvalidConfigurationIdException {
        var configurationIds = IdDispatcher.getConfigurationIds(configurationIdsHex);
        if (!thresholds.isValid()) {
            throw new InvalidThresholdsException(thresholds);
        }

        return configurationSeriesService.getConfigurationsExceedingThresholds(
                fieldName,
                thresholds,
                mode,
                influxQueryFieldsResolver.get(measurementName, bucketName),
                configurationIds
        );
    }

    @PostMapping("/exceedingThresholdsForExperiment")
    public SeriesPack getExceedingThresholdsForExperiment(
            @RequestParam String fieldName,
            @RequestParam CriteriaMode mode,
            @RequestParam(required = false) String measurementName,
            @RequestParam(required = false) String bucketName,
            @RequestParam String experimentName,
            @RequestPart(name = "thresholds") Thresholds thresholds
    ) throws InvalidThresholdsException {
        if (!thresholds.isValid()) {
            throw new InvalidThresholdsException(thresholds);
        }

        return configurationSeriesService.getConfigurationsExceedingThresholdsForExperiment(
                fieldName,
                thresholds,
                mode,
                influxQueryFieldsResolver.get(measurementName, bucketName),
                experimentName
        );
    }

}
