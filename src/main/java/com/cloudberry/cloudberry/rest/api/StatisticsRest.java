package com.cloudberry.cloudberry.rest.api;

import com.cloudberry.cloudberry.analytics.model.DataSeries;
import com.cloudberry.cloudberry.analytics.model.OptimizationGoal;
import com.cloudberry.cloudberry.analytics.model.OptimizationKind;
import com.cloudberry.cloudberry.rest.exceptions.InvalidConfigurationIdException;
import com.cloudberry.cloudberry.rest.exceptions.InvalidComputationIdException;
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

    private final StatisticsService statisticsService;

    @PostMapping("/compare/computations")
    public List<DataSeries> getComputationsByIds(@RequestParam String comparedField,
                                                 @RequestParam(required = false) String measurementName,
                                                 @RequestParam(required = false) String bucketName,
                                                 @RequestBody List<String> computationIdsHex
    ) throws InvalidComputationIdException {
        var computationIds = getValidIds(computationIdsHex);
        if (computationIds.isEmpty())
            throw new InvalidComputationIdException(computationIdsHex);

        return statisticsService.getComputationsByIds(
                comparedField,
                measurementName,
                bucketName,
                computationIds,
                true
        );
    }

    @PostMapping("/compare/computations/all")
    public List<DataSeries> getComputationsByConfigurationId(@RequestParam String comparedField,
                                                             @RequestParam(required = false) String measurementName,
                                                             @RequestParam(required = false) String bucketName,
                                                             @RequestParam String configurationIdHex
    ) throws InvalidConfigurationIdException {
        var configurationId = Optional.of(configurationIdHex)
                .filter(ObjectId::isValid)
                .map(ObjectId::new)
                .orElseThrow(() -> new InvalidConfigurationIdException(List.of(configurationIdHex)));

        return statisticsService.getComputationsByConfigurationId(
                comparedField,
                measurementName,
                bucketName,
                configurationId,
                true
        );
    }

    @PostMapping("/compare/configurations")
    public List<DataSeries> getConfigurationsMeansByIds(@RequestParam String comparedField,
                                                        @RequestParam(required = false) String measurementName,
                                                        @RequestParam(required = false) String bucketName,
                                                        @RequestBody List<String> configurationIdsHex
    ) throws InvalidConfigurationIdException {
        var configurationIds = configurationIdsHex.stream()
                .filter(ObjectId::isValid)
                .map(ObjectId::new)
                .collect(Collectors.toList());

        if (configurationIds.isEmpty())
            throw new InvalidConfigurationIdException(configurationIdsHex);

        return statisticsService.getConfigurationsMeansByIds(
                comparedField,
                measurementName,
                bucketName,
                configurationIds
        );
    }

    @PostMapping("/compare/configurations/all")
    public List<DataSeries> getConfigurationsMeansByExperimentName(@RequestParam String comparedField,
                                                                   @RequestParam(required = false) String measurementName,
                                                                   @RequestParam(required = false) String bucketName,
                                                                   @RequestParam String experimentName
    ) {
        return statisticsService.getConfigurationsMeansByExperimentName(
                comparedField,
                measurementName,
                bucketName,
                experimentName
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
                optimizationGoal,
                optimizationKind,
                measurementName,
                bucketName
        );
    }

    @PostMapping("/computations/averageStddev")
    public List<DataSeries> computationsAverageAndStddev(@RequestParam String fieldName,
                                                         @RequestParam Long interval,
                                                         @RequestParam ChronoUnit unit,
                                                         @RequestParam(required = false) String measurementName,
                                                         @RequestParam(required = false) String bucketName,
                                                         @RequestBody List<String> computationIdsHex
    ) throws InvalidComputationIdException {
        var computationIds = getValidIds(computationIdsHex);
        if (computationIds.isEmpty())
            throw new InvalidComputationIdException(computationIdsHex);

        return statisticsService.computationsAverageAndStddev(
                fieldName,
                interval,
                unit,
                computationIds,
                measurementName,
                bucketName
        );
    }

    private static List<ObjectId> getValidIds(List<String> rawIds) {
        return rawIds.stream()
                .filter(ObjectId::isValid)
                .map(ObjectId::new)
                .collect(Collectors.toList());
    }
}
