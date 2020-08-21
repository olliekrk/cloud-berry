package com.cloudberry.cloudberry.db.influx.service;

import com.cloudberry.cloudberry.analytics.model.OptionalQueryFields;
import com.cloudberry.cloudberry.common.syntax.CollectionSyntax;
import com.cloudberry.cloudberry.db.influx.InfluxDefaults;
import com.cloudberry.cloudberry.db.influx.util.OffsetsFactory;
import com.influxdb.client.InfluxDBClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class InfluxDataEvictor {
    @Value("${spring.influx2.org}")
    private String defaultOrganization;

    private final InfluxDBClient influxClient;

    public void deleteData(OptionalQueryFields optionalQueryFields,
                           Map<String, String> tags) {
        var start = OffsetsFactory.epoch();
        var stop = OffsetsFactory.now();
        var bucket = optionalQueryFields.getBucketName();
        influxClient.getDeleteApi()
                .delete(start,
                        stop,
                        buildDeletePredicate(optionalQueryFields.getMeasurementNameOptional(), tags),
                        bucket,
                        defaultOrganization);
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
