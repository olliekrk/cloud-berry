package com.cloudberry.cloudberry.rest.api;

import com.cloudberry.cloudberry.analytics.model.InfluxQueryFields;
import com.cloudberry.cloudberry.analytics.model.anomalies.AnomalyReport;
import com.cloudberry.cloudberry.rest.exceptions.invalid.id.InvalidComputationIdException;
import com.cloudberry.cloudberry.rest.util.IdDispatcher;
import com.cloudberry.cloudberry.service.api.AnomaliesService;
import com.cloudberry.cloudberry.service.utility.BucketNameResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/anomalies")
@RequiredArgsConstructor
public class AnomaliesRest {
    private final BucketNameResolver bucketNameResolver;
    private final AnomaliesService anomaliesService;

    @PostMapping("/report/bulk")
    public List<AnomalyReport> getReports(@RequestParam String fieldName,
                                          @RequestParam(required = false) String measurementName,
                                          @RequestParam(required = false) String bucketName,
                                          @RequestBody List<String> computationIdsHex
    ) throws InvalidComputationIdException {
        var computationIds = IdDispatcher.getComputationIds(computationIdsHex);

        return anomaliesService.getReports(
                fieldName,
                computationIds,
                getInfluxQueryFields(measurementName, bucketName)
        );
    }

    private InfluxQueryFields getInfluxQueryFields(@Nullable String measurementName,
                                                   @Nullable String bucketName) {
        return new InfluxQueryFields(measurementName, bucketNameResolver.getBucketNameOrDefault(bucketName));
    }
}
