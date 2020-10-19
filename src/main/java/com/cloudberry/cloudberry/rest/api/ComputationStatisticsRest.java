package com.cloudberry.cloudberry.rest.api;

import com.cloudberry.cloudberry.analytics.model.CriteriaMode;
import com.cloudberry.cloudberry.analytics.model.DataSeries;
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
import lombok.val;
import org.springframework.web.bind.annotation.*;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("statistics/computations")
@RequiredArgsConstructor
public class ComputationStatisticsRest {
    private final InfluxQueryFieldsResolver influxQueryFieldsResolver;
    private final ComputationStatisticsService computationStatisticsService;

    @PostMapping("/comparison")
    public List<DataSeries> getComputationsByIds(@RequestParam String fieldName,
                                                 @RequestParam(required = false) String measurementName,
                                                 @RequestParam(required = false) String bucketName,
                                                 @RequestBody List<String> computationIdsHex
    ) throws InvalidComputationIdException {
        var computationIds = IdDispatcher.getComputationIds(computationIdsHex);

        return computationStatisticsService.getComputationsByIds(
                fieldName,
                influxQueryFieldsResolver.get(measurementName, bucketName),
                computationIds,
                true
        );
    }

    @PostMapping("/comparisonForConfiguration")
    public List<DataSeries> getComputationsByConfigurationId(@RequestParam String fieldName,
                                                             @RequestParam(required = false) String measurementName,
                                                             @RequestParam(required = false) String bucketName,
                                                             @RequestParam String configurationIdHex
    ) throws InvalidConfigurationIdException {
        val configurationId = IdDispatcher.getConfigurationId(configurationIdHex);

        return computationStatisticsService.getComputationsByConfigurationId(
                fieldName,
                influxQueryFieldsResolver.get(measurementName, bucketName),
                configurationId,
                true
        );
    }

    @GetMapping("/best")
    public List<DataSeries> getNBestComputations(@RequestParam int n,
                                                 @RequestParam String fieldName,
                                                 @RequestParam OptimizationGoal optimizationGoal,
                                                 @RequestParam OptimizationKind optimizationKind,
                                                 @RequestParam(required = false) String measurementName,
                                                 @RequestParam(required = false) String bucketName) {
        return computationStatisticsService.getNBestComputations(
                n,
                fieldName,
                new Optimization(optimizationGoal, optimizationKind),
                influxQueryFieldsResolver.get(measurementName, bucketName)
        );
    }

    @GetMapping("/bestForConfiguration")
    public List<DataSeries> getNBestComputationsForConfiguration(@RequestParam int n,
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

    @PostMapping("/averageStddev")
    public List<DataSeries> getAverageAndStddevOfComputations(@RequestParam String fieldName,
                                                              @RequestParam long interval,
                                                              @RequestParam ChronoUnit unit,
                                                              @RequestParam(required = false) String measurementName,
                                                              @RequestParam(required = false) String bucketName,
                                                              @RequestBody List<String> computationIdsHex
    ) throws InvalidComputationIdException {
        var computationIds = IdDispatcher.getComputationIds(computationIdsHex);

        return computationStatisticsService.getAverageAndStddevOfComputations(
                fieldName,
                new ChronoInterval(interval, unit),
                computationIds,
                influxQueryFieldsResolver.get(measurementName, bucketName)
        );
    }

    @PostMapping("/exceedingThresholds")
    public List<DataSeries> getComputationsExceedingThresholds(@RequestParam String fieldName,
                                                               @RequestParam CriteriaMode mode,
                                                               @RequestParam(required = false) String measurementName,
                                                               @RequestParam(required = false) String bucketName,
                                                               @RequestBody Thresholds thresholds
    ) throws InvalidThresholdsException {
        if (!thresholds.isValid()) {
            throw new InvalidThresholdsException(thresholds);
        }

        return computationStatisticsService.getComputationsExceedingThresholds(
                fieldName,
                thresholds,
                mode,
                influxQueryFieldsResolver.get(measurementName, bucketName)
        );
    }

    @PostMapping("/exceedingThresholdsForConfiguration")
    public List<DataSeries> getComputationsExceedingThresholds(@RequestParam String fieldName,
                                                               @RequestParam CriteriaMode mode,
                                                               @RequestParam String configurationIdHex,
                                                               @RequestParam(required = false) String measurementName,
                                                               @RequestParam(required = false) String bucketName,
                                                               @RequestBody Thresholds thresholds
    ) throws InvalidThresholdsException, InvalidConfigurationIdException {
        return computationStatisticsService.getComputationsExceedingThresholdsForConfiguration(
                fieldName,
                requireValidThresholds(thresholds),
                mode,
                influxQueryFieldsResolver.get(measurementName, bucketName),
                IdDispatcher.getConfigurationId(configurationIdHex)
        );
    }

    @PostMapping("/exceedingThresholdsRelatively")
    public List<DataSeries> getComputationsExceedingThresholdsRelatively(
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
                requireValidThresholds(thresholds),
                thresholdsType,
                mode,
                influxQueryFieldsResolver.get(measurementName, bucketName),
                IdDispatcher.getConfigurationId(configurationIdHex)
        );
    }

    private Thresholds requireValidThresholds(Thresholds thresholds) throws InvalidThresholdsException {
        return Optional.ofNullable(thresholds)
                .filter(Thresholds::isValid)
                .orElseThrow(() -> new InvalidThresholdsException(thresholds));
    }


}
