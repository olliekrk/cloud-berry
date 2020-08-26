package com.cloudberry.cloudberry.rest.api;

import com.cloudberry.cloudberry.analytics.model.InfluxQueryFields;
import com.cloudberry.cloudberry.analytics.model.anomalies.AnomalyReport;
import com.cloudberry.cloudberry.rest.exceptions.InvalidComputationIdException;
import com.cloudberry.cloudberry.rest.util.RestParametersUtil;
import com.cloudberry.cloudberry.service.BucketNameResolver;
import com.cloudberry.cloudberry.service.api.AnomaliesService;
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
        var computationIds = RestParametersUtil.getValidIds(computationIdsHex);
        if (computationIds.isEmpty())
            throw new InvalidComputationIdException(computationIdsHex);

        return anomaliesService.getReports(
                fieldName,
                computationIds,
                getInfluxQueryFields(measurementName, bucketName)
        );
    }

    private InfluxQueryFields getInfluxQueryFields(@Nullable String measurementName,
                                                   @Nullable String bucketName) {
        return new InfluxQueryFields(measurementName, bucketNameResolver.getOrDefault(bucketName));
    }
}