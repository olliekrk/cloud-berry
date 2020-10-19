package com.cloudberry.cloudberry.rest.api;

import com.cloudberry.cloudberry.analytics.model.anomalies.AnomalyReport;
import com.cloudberry.cloudberry.rest.exceptions.invalid.id.InvalidComputationIdException;
import com.cloudberry.cloudberry.rest.util.IdDispatcher;
import com.cloudberry.cloudberry.service.api.AnomaliesService;
import com.cloudberry.cloudberry.service.utility.InfluxQueryFieldsResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/anomalies")
@RequiredArgsConstructor
public class AnomaliesRest {
    private final InfluxQueryFieldsResolver influxQueryFieldsResolver;
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
                influxQueryFieldsResolver.get(measurementName, bucketName)
        );
    }

}
