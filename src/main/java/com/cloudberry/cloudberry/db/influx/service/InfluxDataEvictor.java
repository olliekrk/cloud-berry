package com.cloudberry.cloudberry.db.influx.service;

import com.cloudberry.cloudberry.db.influx.InfluxDefaults;
import com.cloudberry.cloudberry.db.influx.util.OffsetsFactory;
import com.cloudberry.cloudberry.common.syntax.CollectionSyntax;
import com.influxdb.client.InfluxDBClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class InfluxDataEvictor {
    @Value("${influx.buckets.default-logs}")
    private String defaultBucketName;
    @Value("${spring.influx2.org}")
    private String defaultOrganization;

    private final InfluxDBClient influxClient;

    public void deleteData(@Nullable String bucketName,
                           @Nullable String measurementName,
                           Map<String, String> tags) {
        var start = OffsetsFactory.epoch();
        var stop = OffsetsFactory.now();
        var bucket = Optional.ofNullable(bucketName).orElse(defaultBucketName);
        influxClient.getDeleteApi()
                .delete(start, stop, buildDeletePredicate(measurementName, tags), bucket, defaultOrganization);
    }

    private static String buildDeletePredicate(@Nullable String measurementName,
                                               Map<String, String> tags) {
        // build predicates set using tags
        var predicates = CollectionSyntax.mapped(
                tags.entrySet(),
                entry -> String.format("%s=\"%s\"", entry.getKey(), entry.getValue()),
                HashSet::new
        );

        // add measurement name predicate if provided
        Optional.ofNullable(measurementName)
                .map(name -> String.format("%s=\"%s\"", InfluxDefaults.Columns.MEASUREMENT, name))
                .ifPresent(predicates::add);

        return String.join(" and ", predicates);
    }

}
