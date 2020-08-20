package com.cloudberry.cloudberry.db.influx.service;

import com.cloudberry.cloudberry.analytics.model.OptionalQueryFields;
import com.cloudberry.cloudberry.common.syntax.SetSyntax;
import com.cloudberry.cloudberry.db.influx.InfluxDefaults;
import com.cloudberry.cloudberry.db.influx.util.RestrictionsFactory;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import com.influxdb.query.dsl.Flux;
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

    private final InfluxDBClient influxClient;

    public List<FluxRecord> findData(OptionalQueryFields optionalQueryFields,
                                     Map<String, Object> fields,
                                     Map<String, String> tags) {
        var bucket = optionalQueryFields.getBucketNameOptional().orElse(defaultLogsBucketName);
        var api = influxClient.getQueryApi();

        var measurementRestriction = optionalQueryFields.getMeasurementNameOptional().map(RestrictionsFactory::measurement);
        var columnRestrictions = RestrictionsFactory.everyColumn(fields);
        var tagRestrictions = RestrictionsFactory.everyTag(tags);
        var tagNames = tags.keySet();

        Flux query = Flux.from(bucket).range(Instant.EPOCH);
        query = measurementRestriction.<Flux>map(query::filter).orElse(query);
        query = tagRestrictions.<Flux>map(query::filter).orElse(query);
        query = query.pivot(
                SetSyntax.with(tagNames, InfluxDefaults.Columns.TIME),
                Set.of(InfluxDefaults.Columns.FIELD),
                InfluxDefaults.Columns.VALUE);
        query = columnRestrictions.<Flux>map(query::filter).orElse(query);
        query = query.drop(InfluxDefaults.EXCLUDED_COLUMNS);

        return api.query(query.toString())
                .stream()
                .map(FluxTable::getRecords)
                .flatMap(List::stream)
                .peek(record -> record.getValues().keySet().removeAll(InfluxDefaults.EXCLUDED_COLUMNS))
                .collect(Collectors.toList());
    }
}
