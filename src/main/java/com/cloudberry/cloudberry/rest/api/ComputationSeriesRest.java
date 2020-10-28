package com.cloudberry.cloudberry.rest.api;

import com.cloudberry.cloudberry.analytics.model.CriteriaMode;
import com.cloudberry.cloudberry.analytics.model.dto.SeriesPack;
import com.cloudberry.cloudberry.analytics.model.optimization.Optimization;
import com.cloudberry.cloudberry.analytics.model.optimization.OptimizationGoal;
import com.cloudberry.cloudberry.analytics.model.optimization.OptimizationKind;
import com.cloudberry.cloudberry.analytics.model.thresholds.Thresholds;
import com.cloudberry.cloudberry.analytics.model.thresholds.ThresholdsType;
import com.cloudberry.cloudberry.rest.exceptions.InvalidThresholdsException;
import com.cloudberry.cloudberry.rest.exceptions.invalid.id.InvalidComputationIdException;
import com.cloudberry.cloudberry.rest.exceptions.invalid.id.InvalidConfigurationIdException;
import com.cloudberry.cloudberry.rest.util.IdDispatcher;
import com.cloudberry.cloudberry.service.api.ComputationSeriesService;
import com.cloudberry.cloudberry.service.utility.InfluxQueryFieldsResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("series/computations")
public class ComputationSeriesRest {
    private final InfluxQueryFieldsResolver influxQueryFieldsResolver;
    private final ComputationSeriesService computationSeriesService;

    @PostMapping("/comparison")
    public SeriesPack getByIds(
            @RequestParam String fieldName,
            @RequestParam(required = false) String measurementName,
            @RequestParam(required = false) String bucketName,
            @RequestBody List<String> computationIdsHex
    ) throws InvalidComputationIdException {
        return computationSeriesService.getComputations(
                fieldName,
                influxQueryFieldsResolver.get(measurementName, bucketName),
                IdDispatcher.getComputationIds(computationIdsHex)
        );
    }

    @PostMapping("/comparisonForConfiguration")
    public SeriesPack getByConfigurationId(
            @RequestParam String fieldName,
            @RequestParam(required = false) String measurementName,
            @RequestParam(required = false) String bucketName,
            @RequestParam String configurationIdHex
    ) throws InvalidConfigurationIdException {
        return computationSeriesService.getComputations(
                fieldName,
                influxQueryFieldsResolver.get(measurementName, bucketName),
                IdDispatcher.getConfigurationId(configurationIdHex)
        );
    }

    @PostMapping("/best")
    public SeriesPack getBest(
            @RequestParam int n,
            @RequestParam String fieldName,
            @RequestParam OptimizationGoal optimizationGoal,
            @RequestParam OptimizationKind optimizationKind,
            @RequestParam(required = false) String measurementName,
            @RequestParam(required = false) String bucketName
    ) {
        return computationSeriesService.getNBestComputations(
                n,
                fieldName,
                new Optimization(optimizationGoal, optimizationKind),
                influxQueryFieldsResolver.get(measurementName, bucketName)
        );
    }

    @PostMapping("/bestForConfiguration")
    public SeriesPack getBestForConfiguration(
            @RequestParam int n,
            @RequestParam String fieldName,
            @RequestParam OptimizationGoal optimizationGoal,
            @RequestParam OptimizationKind optimizationKind,
            @RequestParam String configurationIdHex,
            @RequestParam(required = false) String measurementName,
            @RequestParam(required = false) String bucketName
    ) throws InvalidConfigurationIdException {
        return computationSeriesService.getNBestComputationsForConfiguration(
                n,
                fieldName,
                new Optimization(optimizationGoal, optimizationKind),
                influxQueryFieldsResolver.get(measurementName, bucketName),
                IdDispatcher.getConfigurationId(configurationIdHex)
        );
    }

    @PostMapping("/exceedingThresholds")
    public SeriesPack getExceedingThresholdsForConfiguration(
            @RequestParam String fieldName,
            @RequestParam CriteriaMode mode,
            @RequestParam(required = false) String measurementName,
            @RequestParam(required = false) String bucketName,
            @RequestBody Thresholds thresholds
    ) throws InvalidThresholdsException {
        return computationSeriesService.getComputationsExceedingThresholds(
                fieldName,
                thresholds.requireValid(),
                mode,
                influxQueryFieldsResolver.get(measurementName, bucketName)
        );
    }

    @PostMapping("/exceedingThresholdsForConfiguration")
    public SeriesPack getExceedingThresholdsForConfiguration(
            @RequestParam String fieldName,
            @RequestParam CriteriaMode mode,
            @RequestParam String configurationIdHex,
            @RequestParam(required = false) String measurementName,
            @RequestParam(required = false) String bucketName,
            @RequestBody Thresholds thresholds
    ) throws InvalidThresholdsException, InvalidConfigurationIdException {
        return computationSeriesService.getComputationsExceedingThresholdsForConfiguration(
                fieldName,
                thresholds.requireValid(),
                mode,
                influxQueryFieldsResolver.get(measurementName, bucketName),
                IdDispatcher.getConfigurationId(configurationIdHex)
        );
    }

    @PostMapping("/exceedingThresholdsRelatively")
    public SeriesPack getExceedingThresholdsRelatively(
            @RequestParam String fieldName,
            @RequestParam CriteriaMode mode,
            @RequestParam String configurationIdHex,
            @RequestParam(required = false) String measurementName,
            @RequestParam(required = false) String bucketName,
            @RequestParam ThresholdsType thresholdsType,
            @RequestBody Thresholds thresholds
    ) throws InvalidThresholdsException, InvalidConfigurationIdException {
        return computationSeriesService.getComputationsExceedingThresholdsRelatively(
                fieldName,
                thresholds.requireValid(),
                thresholdsType,
                mode,
                influxQueryFieldsResolver.get(measurementName, bucketName),
                IdDispatcher.getConfigurationId(configurationIdHex)
        );
    }

}
