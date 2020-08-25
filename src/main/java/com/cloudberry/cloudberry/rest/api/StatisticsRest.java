package com.cloudberry.cloudberry.rest.api;

import com.cloudberry.cloudberry.analytics.model.*;
import com.cloudberry.cloudberry.analytics.model.optimization.Optimization;
import com.cloudberry.cloudberry.analytics.model.optimization.OptimizationGoal;
import com.cloudberry.cloudberry.analytics.model.optimization.OptimizationKind;
import com.cloudberry.cloudberry.rest.exceptions.InvalidComputationIdException;
import com.cloudberry.cloudberry.rest.exceptions.InvalidConfigurationIdException;
import com.cloudberry.cloudberry.service.BucketNameResolver;
import com.cloudberry.cloudberry.service.api.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.web.bind.annotation.*;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        var computationIds = getValidIds(computationIdsHex);
        if (computationIds.isEmpty())
            throw new InvalidComputationIdException(computationIdsHex);

        return statisticsService.getComputationsByIds(
                fieldName,
                new InfluxQueryFields(measurementName, bucketNameResolver.getOrDefault(bucketName)),
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
        var configurationId = getValidId(configurationIdHex)
                .orElseThrow(() -> new InvalidConfigurationIdException(List.of(configurationIdHex)));

        return statisticsService.getComputationsByConfigurationId(
                fieldName,
                new InfluxQueryFields(measurementName, bucketNameResolver.getOrDefault(bucketName)),
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
                new InfluxQueryFields(measurementName, bucketNameResolver.getOrDefault(bucketName))
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
        var computationIds = getValidIds(computationIdsHex);
        if (computationIds.isEmpty())
            throw new InvalidComputationIdException(computationIdsHex);

        return statisticsService.getAverageAndStddevOfComputations(
                fieldName,
                new ChronoInterval(interval, unit),
                computationIds,
                new InfluxQueryFields(measurementName, bucketNameResolver.getOrDefault(bucketName))
        );
    }

    @PostMapping("/computations/exceedingThresholds")
    public List<DataSeries> getComputationsExceedingThresholds(@RequestParam String fieldName,
                                                               @RequestParam CriteriaMode mode,
                                                               @RequestParam(required = false) String measurementName,
                                                               @RequestParam(required = false) String bucketName,
                                                               @RequestBody Thresholds thresholds) {
        return statisticsService.getComputationsExceedingThresholds(
                fieldName,
                thresholds,
                mode,
                new InfluxQueryFields(measurementName, bucketNameResolver.getOrDefault(bucketName))
        );
    }

    @PostMapping("/configurations/comparison")
    public List<DataSeries> getConfigurationsMeansByIds(@RequestParam String fieldName,
                                                        @RequestParam(required = false) String measurementName,
                                                        @RequestParam(required = false) String bucketName,
                                                        @RequestBody List<String> configurationIdsHex
    ) throws InvalidConfigurationIdException {
        var configurationIds = getValidIds(configurationIdsHex);
        if (configurationIds.isEmpty())
            throw new InvalidConfigurationIdException(configurationIdsHex);

        return statisticsService.getConfigurationsMeansByIds(
                fieldName,
                new InfluxQueryFields(measurementName, bucketNameResolver.getOrDefault(bucketName)),
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
                new InfluxQueryFields(measurementName, bucketNameResolver.getOrDefault(bucketName)),
                experimentName
        );
    }

    private static Optional<ObjectId> getValidId(String rawId) {
        return Optional.of(rawId)
                .filter(ObjectId::isValid)
                .map(ObjectId::new);
    }

    private static List<ObjectId> getValidIds(List<String> rawIds) {
        return rawIds.stream()
                .filter(ObjectId::isValid)
                .map(ObjectId::new)
                .collect(Collectors.toList());
    }
}
