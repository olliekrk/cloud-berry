package com.cloudberry.cloudberry.db.influx.service;

import com.cloudberry.cloudberry.analytics.model.InfluxQueryFields;
import com.cloudberry.cloudberry.common.syntax.CollectionSyntax;
import com.cloudberry.cloudberry.db.influx.InfluxDefaults;
import com.cloudberry.cloudberry.db.influx.util.OffsetsFactory;
import com.influxdb.client.InfluxDBClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class InfluxDataRemover {
    private final InfluxDBClient influxClient;
    private final InfluxOrganizationService influxOrganizationService;

    public void deleteData(InfluxQueryFields influxQueryFields,
                           Map<String, String> tags) {
        var start = OffsetsFactory.epoch();
        var stop = OffsetsFactory.now();
        var bucket = influxQueryFields.getBucketName();
        influxClient.getDeleteApi().delete(
                start,
                stop,
                buildDeletePredicate(influxQueryFields.getMeasurementNameOptional(), tags),
                bucket,
                influxOrganizationService.getDefaultOrganizationId()
        );
    }

    private static String buildDeletePredicate(Optional<String> measurementName,
                                               Map<String, String> tags) {
        // build predicates set using tags
        var predicates = CollectionSyntax.mapped(
                tags.entrySet(),
                entry -> String.format("%s=\"%s\"", entry.getKey(), entry.getValue()),
                HashSet::new
        );

        // add measurement name predicate if provided
        measurementName
                .map(name -> String.format("%s=\"%s\"", InfluxDefaults.Columns.MEASUREMENT, name))
                .ifPresent(predicates::add);

        return String.join(" and ", predicates);
    }

}
