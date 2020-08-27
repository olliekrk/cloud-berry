package com.cloudberry.cloudberry.rest.api;

import com.cloudberry.cloudberry.analytics.model.*;
import com.cloudberry.cloudberry.analytics.model.optimization.Optimization;
import com.cloudberry.cloudberry.analytics.model.optimization.OptimizationGoal;
import com.cloudberry.cloudberry.analytics.model.optimization.OptimizationKind;
import com.cloudberry.cloudberry.rest.exceptions.invalid.id.InvalidComputationIdException;
import com.cloudberry.cloudberry.rest.exceptions.invalid.id.InvalidConfigurationIdException;
import com.cloudberry.cloudberry.rest.exceptions.InvalidThresholdsException;
import com.cloudberry.cloudberry.rest.util.RestParametersUtil;
import com.cloudberry.cloudberry.service.BucketNameResolver;
import com.cloudberry.cloudberry.service.api.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

import java.time.temporal.ChronoUnit;
import java.util.List;

@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
public class StatisticsRest {
    private final BucketNameResolver bucketNameResolver;
    private final StatisticsService statisticsService;

    @PostMapping("/computations/comparison")
    public List<DataSeries> getComputationsByIds(@RequestParam String fieldName,
                                                 @RequestParam(required = false) String measurementName,
                                                 @RequestParam(required = false) String bucketName,
                                                 @RequestBody List<String> computationIdsHex
    ) throws InvalidComputationIdException {
        var computationIds = RestParametersUtil.getValidIds(computationIdsHex);
        if (computationIds.isEmpty())
            throw new InvalidComputationIdException(computationIdsHex);

        return statisticsService.getComputationsByIds(
                fieldName,
                getInfluxQueryFields(measurementName, bucketName),
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
        var configurationId = RestParametersUtil.getValidId(configurationIdHex)
                .orElseThrow(() -> new InvalidConfigurationIdException(List.of(configurationIdHex)));

        return statisticsService.getComputationsByConfigurationId(
                fieldName,
                getInfluxQueryFields(measurementName, bucketName),
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
                getInfluxQueryFields(measurementName, bucketName)
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
        var computationIds = RestParametersUtil.getValidIds(computationIdsHex);
        if (computationIds.isEmpty())
            throw new InvalidComputationIdException(computationIdsHex);

        return statisticsService.getAverageAndStddevOfComputations(
                fieldName,
                new ChronoInterval(interval, unit),
                computationIds,
                getInfluxQueryFields(measurementName, bucketName)
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
                getInfluxQueryFields(measurementName, bucketName)
        );
    }

    @PostMapping("/configurations/comparison")
    public List<DataSeries> getConfigurationsMeansByIds(@RequestParam String fieldName,
                                                        @RequestParam(required = false) String measurementName,
                                                        @RequestParam(required = false) String bucketName,
                                                        @RequestBody List<String> configurationIdsHex
    ) throws InvalidConfigurationIdException {
        var configurationIds = RestParametersUtil.getValidIds(configurationIdsHex);
        if (configurationIds.isEmpty())
            throw new InvalidConfigurationIdException(configurationIdsHex);

        return statisticsService.getConfigurationsMeansByIds(
                fieldName,
                getInfluxQueryFields(measurementName, bucketName),
                configurationIds
        );
    }

    @PostMapping("/configurations/comparison/forExperiment")
    public List<DataSeries> getConfigurationsMeansByExperimentName(@RequestParam String fieldName,
                                                                   @RequestParam(required = false) String measurementName,
                                                                   @RequestParam(required = false) String bucketName,
                                                                   @RequestParam String experimentName
    ) {
        return statisticsService.getConfigurationsMeansByExperimentName(
                fieldName,
                getInfluxQueryFields(measurementName, bucketName),
                experimentName
        );
    }

    private InfluxQueryFields getInfluxQueryFields(@Nullable String measurementName,
                                                   @Nullable String bucketName) {
        return new InfluxQueryFields(measurementName, bucketNameResolver.getOrDefault(bucketName));
    }
}
