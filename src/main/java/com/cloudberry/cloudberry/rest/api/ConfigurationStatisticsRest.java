package com.cloudberry.cloudberry.rest.api;

import com.cloudberry.cloudberry.analytics.model.DataSeries;
import com.cloudberry.cloudberry.analytics.model.InfluxQueryFields;
import com.cloudberry.cloudberry.analytics.model.optimization.Optimization;
import com.cloudberry.cloudberry.analytics.model.optimization.OptimizationGoal;
import com.cloudberry.cloudberry.analytics.model.optimization.OptimizationKind;
import com.cloudberry.cloudberry.rest.exceptions.invalid.id.InvalidConfigurationIdException;
import com.cloudberry.cloudberry.rest.util.IdDispatcher;
import com.cloudberry.cloudberry.service.api.ConfigurationStatisticsService;
import com.cloudberry.cloudberry.service.utility.BucketNameResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("statistics/configurations")
public class ConfigurationStatisticsRest {
    private final ConfigurationStatisticsService configurationStatisticsService;
    private final BucketNameResolver bucketNameResolver;

    @PostMapping("/best")
    public List<DataSeries> getNBestConfigurations(
            @RequestParam int n,
            @RequestParam String fieldName,
            @RequestParam OptimizationGoal optimizationGoal,
            @RequestParam OptimizationKind optimizationKind,
            @RequestParam(required = false) String measurementName,
            @RequestParam(required = false) String bucketName,
            @RequestBody List<String> configurationIdsHex
    ) throws InvalidConfigurationIdException {
        var configurationIds = IdDispatcher.getConfigurationIds(configurationIdsHex);
        return configurationStatisticsService.getNBestConfigurations(
                n,
                fieldName,
                new Optimization(optimizationGoal, optimizationKind),
                bucketNameResolver.constructQueryFields(measurementName, bucketName),
                configurationIds
        );
    }

    @PostMapping("/bestForExperiment")
    public List<DataSeries> getNBestConfigurationsForExperiment(
            @RequestParam int n,
            @RequestParam String fieldName,
            @RequestParam OptimizationGoal optimizationGoal,
            @RequestParam OptimizationKind optimizationKind,
            @RequestParam(required = false) String measurementName,
            @RequestParam(required = false) String bucketName,
            @RequestParam String experimentName
    ) {
        return configurationStatisticsService.getNBestConfigurationsForExperiment(
                n,
                fieldName,
                new Optimization(optimizationGoal, optimizationKind),
                new InfluxQueryFields(measurementName, bucketNameResolver.getBucketNameOrDefault(bucketName)),
                experimentName
        );
    }

    @PostMapping("/comparison")
    public List<DataSeries> getConfigurationsMeans(
            @RequestParam String fieldName,
            @RequestParam(required = false) String measurementName,
            @RequestParam(required = false) String bucketName,
            @RequestBody List<String> configurationIdsHex
    ) throws InvalidConfigurationIdException {
        var configurationIds = IdDispatcher.getConfigurationIds(configurationIdsHex);
        return configurationStatisticsService.getConfigurationsMeans(
                fieldName,
                bucketNameResolver.constructQueryFields(measurementName, bucketName),
                configurationIds
        );
    }

    @PostMapping("/comparisonForExperiment")
    public List<DataSeries> getConfigurationsMeansForExperiment(
            @RequestParam String fieldName,
            @RequestParam(required = false) String measurementName,
            @RequestParam(required = false) String bucketName,
            @RequestParam String experimentName
    ) {
        return configurationStatisticsService.getConfigurationsMeansForExperiment(
                fieldName,
                bucketNameResolver.constructQueryFields(measurementName, bucketName),
                experimentName
        );
    }
}
