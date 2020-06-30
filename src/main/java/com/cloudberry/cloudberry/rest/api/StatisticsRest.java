package com.cloudberry.cloudberry.rest.api;

import com.cloudberry.cloudberry.service.api.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
public class StatisticsRest {

    private final StatisticsService statisticsService;

    /**
     * @param comparedField      name of the numeric field to compare
     * @param measurementName    measurement name
     * @param bucketName         custom data bucket name
     * @param metadataBucketName custom metadata bucket name
     * @param metaTags           tags used to group data, i.e. evaluation_id
     *                           Values are allowed set of tag values to filter measurements by.
     *                           If the list is empty, no filters are applied for that tag.
     */
    @PostMapping("/grouped/compare")
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
