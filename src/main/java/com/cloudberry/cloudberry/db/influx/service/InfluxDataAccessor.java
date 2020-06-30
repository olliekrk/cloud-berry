package com.cloudberry.cloudberry.db.influx.service;

import com.cloudberry.cloudberry.db.influx.InfluxDefaults;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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

        var measurementRestriction = Restrictions.measurement().equal(measurementName);
        var columnRestrictions = getCompoundColumnRestrictions(fields);
        var tagRestrictions = getCompoundTagRestrictions(tags);
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

    private static Optional<Restrictions> getCompoundTagRestrictions(Map<String, String> tags) {
        var tagRestrictions = tags
                .entrySet()
                .stream()
                .map(entry -> Restrictions.tag(entry.getKey()).equal(entry.getValue()))
                .toArray(Restrictions[]::new);

        return tagRestrictions.length == 0 ? Optional.empty() : Optional.of(Restrictions.and(tagRestrictions));
    }

    private static Optional<Restrictions> getCompoundColumnRestrictions(Map<String, Object> fields) {
        var fieldRestrictions = fields
                .entrySet()
                .stream()
                .map(entry -> Restrictions.column(entry.getKey()).equal(entry.getValue()))
                .toArray(Restrictions[]::new);

        return fieldRestrictions.length == 0 ? Optional.empty() : Optional.of(Restrictions.and(fieldRestrictions));
    }

    private static Optional<Restrictions> getCompoundFieldRestrictions(Map<String, Object> fields) {
        var fieldRestrictions = fields
                .entrySet()
                .stream()
                .map(entry -> Restrictions.and(
                        Restrictions.field().equal(entry.getKey()),
                        Restrictions.value().equal(entry.getValue())))
                .toArray(Restrictions[]::new);

        return fieldRestrictions.length == 0 ? Optional.empty() : Optional.of(Restrictions.or(fieldRestrictions));
    }
}
