package com.cloudberry.cloudberry.rest.api;

import com.cloudberry.cloudberry.model.statistics.DataSeries;
import com.cloudberry.cloudberry.rest.exceptions.InvalidConfigurationIdException;
import com.cloudberry.cloudberry.rest.exceptions.InvalidEvaluationIdException;
import com.cloudberry.cloudberry.service.api.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
public class StatisticsRest {

    private final StatisticsService statisticsService;

    @PostMapping("/compare/evaluations")
    public List<DataSeries> compareSelectedEvaluations(@RequestParam String comparedField,
                                                       @RequestParam(required = false) String measurementName,
                                                       @RequestParam(required = false) String bucketName,
                                                       @RequestBody List<String> evaluationIdsHex
    ) throws InvalidEvaluationIdException {
        var evaluationIds = evaluationIdsHex.stream()
                .filter(ObjectId::isValid)
                .map(ObjectId::new)
                .collect(Collectors.toList());

        if (evaluationIds.isEmpty())
            throw new InvalidEvaluationIdException(evaluationIdsHex);

        return statisticsService.compareEvaluations(
                comparedField,
                measurementName,
                bucketName,
                evaluationIds,
                true
        );
    }

    @PostMapping("/compare/evaluations/all")
    public List<DataSeries> compareAllEvaluationsForConfiguration(@RequestParam String comparedField,
                                                                  @RequestParam(required = false) String measurementName,
                                                                  @RequestParam(required = false) String bucketName,
                                                                  @RequestParam String configurationIdHex
    ) throws InvalidConfigurationIdException {
        var configurationId = Optional.of(configurationIdHex)
                .filter(ObjectId::isValid)
                .map(ObjectId::new)
                .orElseThrow(() -> new InvalidConfigurationIdException(List.of(configurationIdHex)));

        return statisticsService.compareEvaluationsForConfiguration(
                comparedField,
                measurementName,
                bucketName,
                configurationId,
                true
        );
    }

    @PostMapping("/compare/configurations")
    public List<DataSeries> compareSelectedConfigurations(@RequestParam String comparedField,
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

        return statisticsService.compareConfigurations(
                comparedField,
                measurementName,
                bucketName,
                configurationIds
        );
    }

    @PostMapping("/compare/configurations/all")
    public List<DataSeries> compareAllConfigurationsForExperiment(@RequestParam String comparedField,
                                                                  @RequestParam(required = false) String measurementName,
                                                                  @RequestParam(required = false) String bucketName,
                                                                  @RequestParam String experimentName
    ) {
        return statisticsService.compareConfigurationsForExperiment(
                comparedField,
                measurementName,
                bucketName,
                experimentName
        );
    }
}
