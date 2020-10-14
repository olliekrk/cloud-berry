package com.cloudberry.cloudberry.rest.api;

import com.cloudberry.cloudberry.analytics.model.ChronoInterval;
import com.cloudberry.cloudberry.analytics.model.CriteriaMode;
import com.cloudberry.cloudberry.analytics.model.DataSeries;
import com.cloudberry.cloudberry.analytics.model.Thresholds;
import com.cloudberry.cloudberry.analytics.model.optimization.Optimization;
import com.cloudberry.cloudberry.analytics.model.optimization.OptimizationGoal;
import com.cloudberry.cloudberry.analytics.model.optimization.OptimizationKind;
import com.cloudberry.cloudberry.rest.exceptions.InvalidThresholdsException;
import com.cloudberry.cloudberry.rest.exceptions.invalid.id.InvalidComputationIdException;
import com.cloudberry.cloudberry.rest.exceptions.invalid.id.InvalidConfigurationIdException;
import com.cloudberry.cloudberry.rest.util.IdDispatcher;
import com.cloudberry.cloudberry.service.api.StatisticsService;
import com.cloudberry.cloudberry.service.utility.InfluxQueryFieldsResolver;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.temporal.ChronoUnit;
import java.util.List;

@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
public class StatisticsRest {
    private final InfluxQueryFieldsResolver influxQueryFieldsResolver;
    private final StatisticsService statisticsService;

    @PostMapping("/computations/comparison")
    public List<DataSeries> getComputationsByIds(@RequestParam String fieldName,
                                                 @RequestParam(required = false) String measurementName,
                                                 @RequestParam(required = false) String bucketName,
                                                 @RequestBody List<String> computationIdsHex
    ) throws InvalidComputationIdException {
        var computationIds = IdDispatcher.getComputationIds(computationIdsHex);

        return statisticsService.getComputationsByIds(
                fieldName,
                influxQueryFieldsResolver.get(measurementName, bucketName),
                computationIds,
                true
        );
    }

    @PostMapping("/computations/comparison/forConfiguration")
    public List<DataSeries> getComputationsByConfigurationId(@RequestParam String fieldName,
                                                             @RequestParam(required = false) String measurementName,
                                                             @RequestParam(required = false) String bucketName,
                                                             @RequestParam String configurationIdHex
    ) throws InvalidConfigurationIdException {
        val configurationId = IdDispatcher.getConfigurationId(configurationIdHex);

        return statisticsService.getComputationsByConfigurationId(
                fieldName,
                influxQueryFieldsResolver.get(measurementName, bucketName),
                configurationId,
                true
        );
    }

    @GetMapping("/computations/best")
    public List<DataSeries> getNBestComputations(@RequestParam int n,
                                                 @RequestParam String fieldName,
                                                 @RequestParam OptimizationGoal optimizationGoal,
                                                 @RequestParam OptimizationKind optimizationKind,
                                                 @RequestParam(required = false) String measurementName,
                                                 @RequestParam(required = false) String bucketName) {

        return statisticsService.getNBestComputations(
                n,
                fieldName,
                new Optimization(optimizationGoal, optimizationKind),
                influxQueryFieldsResolver.get(measurementName, bucketName)
        );
    }

    @GetMapping("/computations/best/forConfiguration")
    public List<DataSeries> getNBestComputationsForConfiguration(@RequestParam int n,
                                                                 @RequestParam String fieldName,
                                                                 @RequestParam OptimizationGoal optimizationGoal,
                                                                 @RequestParam OptimizationKind optimizationKind,
                                                                 @RequestParam String configurationIdHex,
                                                                 @RequestParam(required = false) String measurementName,
                                                                 @RequestParam(required = false) String bucketName
    ) throws InvalidConfigurationIdException {

        return statisticsService.getNBestComputationsForConfiguration(
                n,
                fieldName,
                new Optimization(optimizationGoal, optimizationKind),
                influxQueryFieldsResolver.get(measurementName, bucketName),
                IdDispatcher.getConfigurationId(configurationIdHex)
        );
    }

    @PostMapping("/computations/averageStddev")
    public List<DataSeries> getAverageAndStddevOfComputations(@RequestParam String fieldName,
                                                              @RequestParam long interval,
                                                              @RequestParam ChronoUnit unit,
                                                              @RequestParam(required = false) String measurementName,
                                                              @RequestParam(required = false) String bucketName,
                                                              @RequestBody List<String> computationIdsHex
    ) throws InvalidComputationIdException {
        var computationIds = IdDispatcher.getComputationIds(computationIdsHex);

        return statisticsService.getAverageAndStddevOfComputations(
                fieldName,
                new ChronoInterval(interval, unit),
                computationIds,
                influxQueryFieldsResolver.get(measurementName, bucketName)
        );
    }

    @PostMapping("/computations/exceedingThresholds")
    public List<DataSeries> getComputationsExceedingThresholds(@RequestParam String fieldName,
                                                               @RequestParam CriteriaMode mode,
                                                               @RequestParam(required = false) String measurementName,
                                                               @RequestParam(required = false) String bucketName,
                                                               @RequestBody Thresholds thresholds
    ) throws InvalidThresholdsException {
        if (!thresholds.isValid()) {
            throw new InvalidThresholdsException(thresholds);
        }

        return statisticsService.getComputationsExceedingThresholds(
                fieldName,
                thresholds,
                mode,
                influxQueryFieldsResolver.get(measurementName, bucketName)
        );
    }

    @PostMapping("/computations/exceedingThresholds/forConfiguration")
    public List<DataSeries> getComputationsExceedingThresholds(@RequestParam String fieldName,
                                                               @RequestParam CriteriaMode mode,
                                                               @RequestParam String configurationIdHex,
                                                               @RequestParam(required = false) String measurementName,
                                                               @RequestParam(required = false) String bucketName,
                                                               @RequestBody Thresholds thresholds
    ) throws InvalidThresholdsException, InvalidConfigurationIdException {
        if (!thresholds.isValid()) {
            throw new InvalidThresholdsException(thresholds);
        }

        return statisticsService.getComputationsExceedingThresholdsForConfiguration(
                fieldName,
                thresholds,
                mode,
                influxQueryFieldsResolver.get(measurementName, bucketName),
                IdDispatcher.getConfigurationId(configurationIdHex)
        );
    }

    @PostMapping("/configurations/comparison")
    public List<DataSeries> getConfigurationsMeansByIds(@RequestParam String fieldName,
                                                        @RequestParam(required = false) String measurementName,
                                                        @RequestParam(required = false) String bucketName,
                                                        @RequestBody List<String> configurationIdsHex
    ) throws InvalidConfigurationIdException {
        val configurationIds = IdDispatcher.getConfigurationIds(configurationIdsHex);

        return statisticsService.getConfigurationsMeansByIds(
                fieldName,
                influxQueryFieldsResolver.get(measurementName, bucketName),
                configurationIds
        );
    }

    @PostMapping("/configurations/comparison/forExperiment")
    public List<DataSeries> getConfigurationsMeansByExperimentName(@RequestParam String fieldName,
                                                                   @RequestParam(required = false)
                                                                           String measurementName,
                                                                   @RequestParam(required = false) String bucketName,
                                                                   @RequestParam String experimentName
    ) {
        return statisticsService.getConfigurationsMeansByExperimentName(
                fieldName,
                influxQueryFieldsResolver.get(measurementName, bucketName),
                experimentName
        );
    }

}
