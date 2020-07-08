package com.cloudberry.cloudberry.rest.api;

import com.cloudberry.cloudberry.service.api.StatisticsService;
import com.cloudberry.cloudberry.util.syntax.ListSyntax;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
public class StatisticsRest {

    private final StatisticsService statisticsService;

    @PostMapping("/compare/configurations/all")
    public void compareAllConfigurations(@RequestParam String comparedField,
                                         @RequestParam String configurationIdHex,
                                         @RequestParam(required = false) String bucketName) {
        // todo
    }

    @PostMapping("/compare/configurations")
    public void compareSelectedConfigurations(@RequestParam String comparedField,
                                              @RequestParam String configurationIdHex,
                                              @RequestParam(required = false) String bucketName) {
        // todo
    }

    @PostMapping("/compare/evaluations/all")
    public void compareAllEvaluations(@RequestParam String comparedField,
                                      @RequestParam String configurationIdHex,
                                      @RequestParam(required = false) String bucketName) {
        // todo
    }

    @PostMapping("/compare/evaluations")
    public List<List<Map<String, Object>>> compareMultipleEvaluations(@RequestParam String comparedField,
                                                                      @RequestParam String measurementName,
                                                                      @RequestParam(required = false) String bucketName,
                                                                      @RequestBody List<String> evaluationIds) {
        return evaluationIds.isEmpty() ? Collections.emptyList() : statisticsService.compareMultipleEvaluations(
                measurementName,
                bucketName,
                comparedField,
                ListSyntax.mapped(evaluationIds, UUID::fromString)
        );
    }

    /**
     * @param comparedField      name of the numeric field to compare
     * @param measurementName    measurement name
     * @param bucketName         custom data bucket name
     * @param metadataBucketName custom metadata bucket name
     * @param metaTags           tags used to group data, i.e. evaluation_id
     *                           Values are allowed set of tag values to filter measurements by.
     *                           If the list is empty, no filters are applied for that tag.
     */
    @PostMapping("/compare/grouped")
    public void getMeanAndStdOfGroupedData(@RequestParam String comparedField,
                                           @RequestParam String measurementName,
                                           @RequestParam String metaMeasurementName,
                                           @RequestParam(required = false) String bucketName,
                                           @RequestParam(required = false) String metadataBucketName,
                                           @RequestBody Map<String, String> metaTags) {
        statisticsService.getMeanAndStdOfGroupedData(
                comparedField,
                measurementName,
                metaMeasurementName,
                bucketName,
                metadataBucketName,
                metaTags
        );
    }

}
