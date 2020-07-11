package com.cloudberry.cloudberry.db.influx.service;

import com.cloudberry.cloudberry.db.influx.InfluxDefaults;
import com.cloudberry.cloudberry.db.influx.InfluxDefaults;
import com.cloudberry.cloudberry.db.influx.util.RestrictionsFactory;
import com.cloudberry.cloudberry.util.syntax.SetSyntax;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import com.influxdb.query.dsl.Flux;
import com.influxdb.query.dsl.functions.restriction.Restrictions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class InfluxDataAccessor {
    @Value("${influx.buckets.default-logs}")
    private String defaultLogsBucketName;
    @Value("${influx.buckets.default-logs-meta}")
    private String defaultMetaBucketName;

    private final InfluxDBClient influxClient;

    public List<FluxRecord> findMeasurements(@Nullable String bucketName,
                                             String measurementName,
                                             Map<String, Object> fields,
                                             Map<String, String> tags) {
        var bucket = Optional.ofNullable(bucketName).orElse(defaultLogsBucketName);
        var api = influxClient.getQueryApi();

        var measurementRestriction = RestrictionsFactory.measurement(measurementName);
        var columnRestrictions = RestrictionsFactory.everyColumn(fields);
        var tagRestrictions = RestrictionsFactory.everyTag(tags);
        var tagNames = tags.keySet();

        Flux query = Flux.from(bucket).range(Instant.EPOCH).filter(measurementRestriction);
        query = tagRestrictions.isPresent() ? query.filter(tagRestrictions.get()) : query;
        query = query.pivot(
                SetSyntax.with(tagNames, InfluxDefaults.Columns.TIME),
                Set.of(InfluxDefaults.Columns.FIELD),
                InfluxDefaults.Columns.VALUE);
        query = columnRestrictions.isPresent() ? query.filter(columnRestrictions.get()) : query;

        return api.query(query.toString())
                .stream()
                .map(FluxTable::getRecords)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    public List<List<FluxRecord>> getEvaluationsData(String measurementName,
                                                     @Nullable String bucketName,
                                                     String comparedField,
                                                     List<UUID> evaluationIds) {
        var bucket = Optional.ofNullable(bucketName).orElse(defaultLogsBucketName);
        var api = influxClient.getQueryApi();

        var measurementRestriction = RestrictionsFactory.measurement(measurementName);
        var evaluationIdTag = InfluxDefaults.CommonTags.EVALUATION_ID;
        var evaluationIdRestriction = Restrictions.or(evaluationIds
                .stream()
                .map(id -> Restrictions.tag(evaluationIdTag).equal(id.toString()))
                .toArray(Restrictions[]::new));
        var fieldRestriction = Restrictions.field().equal(comparedField);

        Flux query = Flux.from(bucket)
                .range(Instant.EPOCH)
                .filter(Restrictions.and(measurementRestriction, evaluationIdRestriction, fieldRestriction))
                .groupBy(evaluationIdTag)
                .pivot(
                        Set.of(evaluationIdTag, InfluxDefaults.Columns.TIME),
                        Set.of(InfluxDefaults.Columns.FIELD),
                        InfluxDefaults.Columns.VALUE
                )
                .keep(List.of(InfluxDefaults.Columns.TIME, comparedField, evaluationIdTag));

        return api.query(query.toString())
                .stream()
                .map(FluxTable::getRecords)
                .collect(Collectors.toList());
    }

    public void getMeanAndStdOfGroupedData(String comparedField,
                                           String measurementName,
                                           String metaMeasurementName,
                                           @Nullable String bucketName,
                                           @Nullable String metaBucketName,
                                           Map<String, String> metaTags) {
        var metaBucket = Optional.ofNullable(metaBucketName).orElse(defaultMetaBucketName);
        var dataBucket = Optional.ofNullable(bucketName).orElse(defaultLogsBucketName);
        var api = influxClient.getQueryApi();

        Flux query = Flux.from(metaBucket).range(Instant.EPOCH);

    }

}
