package com.cloudberry.cloudberry.db.influx.service;

import com.cloudberry.cloudberry.analytics.model.InfluxQueryFields;
import com.cloudberry.cloudberry.common.syntax.ListSyntax;
import com.cloudberry.cloudberry.common.syntax.SetSyntax;
import com.cloudberry.cloudberry.db.influx.InfluxDefaults;
import com.cloudberry.cloudberry.db.influx.util.FluxQueryUtil;
import com.cloudberry.cloudberry.db.influx.util.RestrictionsFactory;
import com.cloudberry.cloudberry.db.influx.model.DataFilters;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import com.influxdb.query.dsl.Flux;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class InfluxDataAccessor {
    private final InfluxDBClient influxClient;

    public List<FluxRecord> findData(InfluxQueryFields influxQueryFields, DataFilters dataFilters) {
        var bucket = influxQueryFields.getBucketName();

        var measurementRestriction = influxQueryFields.getMeasurementNameOptional().map(RestrictionsFactory::measurement);
        var fieldRestrictions = RestrictionsFactory.anyFieldEquals(dataFilters.getFieldFilters());
        var tagRestrictions = RestrictionsFactory.everyTagEquals(dataFilters.getTagFilters());
        var tagPresenceRestrictions = RestrictionsFactory.hasEveryTag(dataFilters.getTagPresence());
        var allRestrictions = Stream.of(measurementRestriction, tagPresenceRestrictions, tagRestrictions, fieldRestrictions);

        var columnsToKeep = Stream.of(
                dataFilters.getTagFilters().keySet().stream(),
                dataFilters.getTagPresence().stream(),
                Stream.of(InfluxDefaults.Columns.TIME)
        ).flatMap(i -> i).collect(Collectors.toSet());

        Flux query = FluxQueryUtil
                .applyRestrictions(
                        Flux.from(bucket).range(Instant.EPOCH),
                        allRestrictions.flatMap(Optional::stream).collect(Collectors.toList())
                )
                .pivot(columnsToKeep, Set.of(InfluxDefaults.Columns.FIELD), InfluxDefaults.Columns.VALUE)
                .drop(InfluxDefaults.EXCLUDED_COLUMNS);

        return influxClient.getQueryApi()
                .query(query.toString())
                .stream()
                .map(FluxTable::getRecords)
                .flatMap(List::stream)
                .peek(record -> record.getValues().keySet().removeAll(InfluxDefaults.EXCLUDED_COLUMNS))
                .collect(Collectors.toList());
    }
}
