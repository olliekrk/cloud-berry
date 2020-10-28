package com.cloudberry.cloudberry.rest.api;

import com.cloudberry.cloudberry.analytics.model.CriteriaMode;
import com.cloudberry.cloudberry.analytics.model.dto.SeriesPack;
import com.cloudberry.cloudberry.analytics.model.optimization.Optimization;
import com.cloudberry.cloudberry.analytics.model.optimization.OptimizationGoal;
import com.cloudberry.cloudberry.analytics.model.optimization.OptimizationKind;
import com.cloudberry.cloudberry.analytics.model.thresholds.Thresholds;
import com.cloudberry.cloudberry.analytics.model.thresholds.ThresholdsType;
import com.cloudberry.cloudberry.analytics.model.time.ChronoInterval;
import com.cloudberry.cloudberry.rest.exceptions.InvalidThresholdsException;
import com.cloudberry.cloudberry.rest.exceptions.invalid.id.InvalidComputationIdException;
import com.cloudberry.cloudberry.rest.exceptions.invalid.id.InvalidConfigurationIdException;
import com.cloudberry.cloudberry.rest.util.IdDispatcher;
import com.cloudberry.cloudberry.service.api.ComputationStatisticsService;
import com.cloudberry.cloudberry.service.utility.InfluxQueryFieldsResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.temporal.ChronoUnit;
import java.util.List;

@RestController
@RequestMapping("statistics/computations")
@RequiredArgsConstructor
public class ComputationStatisticsRest {
    private final InfluxQueryFieldsResolver influxQueryFieldsResolver;
    private final ComputationStatisticsService computationStatisticsService;

    @PostMapping("/comparison")
    public SeriesPack getComputationsByIds(
            @RequestParam String fieldName,
            @RequestParam(required = false) String measurementName,
            @RequestParam(required = false) String bucketName,
            @RequestBody List<String> computationIdsHex
    ) throws InvalidComputationIdException {
        return computationStatisticsService.getComputationsByIds(
                fieldName,
                influxQueryFieldsResolver.get(measurementName, bucketName),
                IdDispatcher.getComputationIds(computationIdsHex)
        );
    }

    @PostMapping("/comparisonForConfiguration")
    public SeriesPack getComputationsByConfigurationId(
            @RequestParam String fieldName,
            @RequestParam(required = false) String measurementName,
            @RequestParam(required = false) String bucketName,
            @RequestParam String configurationIdHex
    ) throws InvalidConfigurationIdException {
        return computationStatisticsService.getComputationsByConfigurationId(
                fieldName,
                influxQueryFieldsResolver.get(measurementName, bucketName),
                IdDispatcher.getConfigurationId(configurationIdHex)
        );
    }

    @GetMapping("/best")
    public SeriesPack getNBestComputations(
            @RequestParam int n,
            @RequestParam String fieldName,
            @RequestParam OptimizationGoal optimizationGoal,
            @RequestParam OptimizationKind optimizationKind,
            @RequestParam(required = false) String measurementName,
            @RequestParam(required = false) String bucketName
    ) {
        return computationStatisticsService.getNBestComputations(
                n,
                fieldName,
                new Optimization(optimizationGoal, optimizationKind),
                influxQueryFieldsResolver.get(measurementName, bucketName)
        );
    }

    @GetMapping("/bestForConfiguration")
    public SeriesPack getNBestComputationsForConfiguration(
            @RequestParam int n,
            @RequestParam String fieldName,
            @RequestParam OptimizationGoal optimizationGoal,
            @RequestParam OptimizationKind optimizationKind,
            @RequestParam String configurationIdHex,
            @RequestParam(required = false) String measurementName,
            @RequestParam(required = false) String bucketName
    ) throws InvalidConfigurationIdException {
        return computationStatisticsService.getNBestComputationsForConfiguration(
                n,
                fieldName,
                new Optimization(optimizationGoal, optimizationKind),
                influxQueryFieldsResolver.get(measurementName, bucketName),
                IdDispatcher.getConfigurationId(configurationIdHex)
        );
    }


    // todo: remove completely?
    @PostMapping("/averageStddev")
    public SeriesPack getAverageAndStddevOfComputations(
            @RequestParam String fieldName,
            @RequestParam long interval,
            @RequestParam ChronoUnit unit,
            @RequestParam(required = false) String measurementName,
            @RequestParam(required = false) String bucketName,
            @RequestBody List<String> computationIdsHex
    ) throws InvalidComputationIdException {
        return computationStatisticsService.getAverageAndStddevOfComputations(
                fieldName,
                new ChronoInterval(interval, unit),
                IdDispatcher.getComputationIds(computationIdsHex),
                influxQueryFieldsResolver.get(measurementName, bucketName)
        );
    }

    @PostMapping("/exceedingThresholds")
    public SeriesPack getComputationsExceedingThresholds(
            @RequestParam String fieldName,
            @RequestParam CriteriaMode mode,
            @RequestParam(required = false) String measurementName,
            @RequestParam(required = false) String bucketName,
            @RequestBody Thresholds thresholds
    ) throws InvalidThresholdsException {
        return computationStatisticsService.getComputationsExceedingThresholds(
                fieldName,
                thresholds.requireValid(),
                mode,
                influxQueryFieldsResolver.get(measurementName, bucketName)
        );
    }

    @PostMapping("/exceedingThresholdsForConfiguration")
    public SeriesPack getComputationsExceedingThresholds(
            @RequestParam String fieldName,
            @RequestParam CriteriaMode mode,
            @RequestParam String configurationIdHex,
            @RequestParam(required = false) String measurementName,
            @RequestParam(required = false) String bucketName,
            @RequestBody Thresholds thresholds
    ) throws InvalidThresholdsException, InvalidConfigurationIdException {
        return computationStatisticsService.getComputationsExceedingThresholdsForConfiguration(
                fieldName,
                thresholds.requireValid(),
                mode,
                influxQueryFieldsResolver.get(measurementName, bucketName),
                IdDispatcher.getConfigurationId(configurationIdHex)
        );
    }

    @PostMapping("/exceedingThresholdsRelatively")
    public SeriesPack getComputationsExceedingThresholdsRelatively(
            @RequestParam String fieldName,
            @RequestParam CriteriaMode mode,
            @RequestParam String configurationIdHex,
            @RequestParam(required = false) String measurementName,
            @RequestParam(required = false) String bucketName,
            @RequestParam ThresholdsType thresholdsType,
            @RequestBody Thresholds thresholds
    ) throws InvalidThresholdsException, InvalidConfigurationIdException {
        return computationStatisticsService.getComputationsExceedingThresholdsRelatively(
                fieldName,
                thresholds.requireValid(),
                thresholdsType,
                mode,
                influxQueryFieldsResolver.get(measurementName, bucketName),
                IdDispatcher.getConfigurationId(configurationIdHex)
        );
    }

}
