package com.cloudberry.cloudberry.rest.api;

import com.cloudberry.cloudberry.analytics.model.CriteriaMode;
import com.cloudberry.cloudberry.analytics.model.dto.SeriesPack;
import com.cloudberry.cloudberry.analytics.model.filters.DataFilters;
import com.cloudberry.cloudberry.analytics.model.filters.DataFiltersWithIds;
import com.cloudberry.cloudberry.analytics.model.filters.DataFiltersWithThresholds;
import com.cloudberry.cloudberry.analytics.model.filters.DataFiltersWithThresholdsAndIds;
import com.cloudberry.cloudberry.analytics.model.optimization.Optimization;
import com.cloudberry.cloudberry.analytics.model.optimization.OptimizationGoal;
import com.cloudberry.cloudberry.analytics.model.optimization.OptimizationKind;
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
import org.springframework.web.bind.annotation.RestController;

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
            @RequestBody DataFiltersWithIds payload
    ) throws InvalidConfigurationIdException {
        return configurationSeriesService.getConfigurations(
                fieldName,
                influxQueryFieldsResolver.get(measurementName, bucketName),
                IdDispatcher.requireConfigurationIds(payload.getIds()),
                payload.getFilters()
        );
    }

    @PostMapping("/comparisonForExperiment")
    public SeriesPack getByExperimentName(
            @RequestParam String fieldName,
            @RequestParam(required = false) String measurementName,
            @RequestParam(required = false) String bucketName,
            @RequestParam String experimentName,
            @RequestBody DataFilters filters
    ) {
        return configurationSeriesService.getConfigurationsForExperiment(
                fieldName,
                influxQueryFieldsResolver.get(measurementName, bucketName),
                experimentName,
                filters
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
            @RequestBody DataFiltersWithIds payload
    ) throws InvalidConfigurationIdException {
        return configurationSeriesService.getNBestConfigurations(
                n,
                fieldName,
                new Optimization(optimizationGoal, optimizationKind),
                influxQueryFieldsResolver.get(measurementName, bucketName),
                IdDispatcher.requireConfigurationIds(payload.getIds()),
                payload.getFilters()
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
            @RequestParam String experimentName,
            @RequestBody DataFilters filters
    ) {
        return configurationSeriesService.getNBestConfigurationsForExperiment(
                n,
                fieldName,
                new Optimization(optimizationGoal, optimizationKind),
                influxQueryFieldsResolver.get(measurementName, bucketName),
                experimentName,
                filters
        );
    }

    @PostMapping("/exceedingThresholds")
    public SeriesPack getExceedingThresholds(
            @RequestParam String fieldName,
            @RequestParam CriteriaMode mode,
            @RequestParam(required = false) String measurementName,
            @RequestParam(required = false) String bucketName,
            @RequestBody DataFiltersWithThresholdsAndIds payload
    ) throws InvalidThresholdsException, InvalidConfigurationIdException {
        return configurationSeriesService.getConfigurationsExceedingThresholds(
                fieldName,
                payload.getThresholds(),
                mode,
                influxQueryFieldsResolver.get(measurementName, bucketName),
                IdDispatcher.requireConfigurationIds(payload.getIds()),
                payload.getFilters()
        );
    }

    @PostMapping("/exceedingThresholdsForExperiment")
    public SeriesPack getExceedingThresholdsForExperiment(
            @RequestParam String fieldName,
            @RequestParam CriteriaMode mode,
            @RequestParam(required = false) String measurementName,
            @RequestParam(required = false) String bucketName,
            @RequestParam String experimentName,
            @RequestBody DataFiltersWithThresholds payload
    ) throws InvalidThresholdsException {
        return configurationSeriesService.getConfigurationsExceedingThresholdsForExperiment(
                fieldName,
                payload.getThresholds(),
                mode,
                influxQueryFieldsResolver.get(measurementName, bucketName),
                experimentName,
                payload.getFilters()
        );
    }

}
