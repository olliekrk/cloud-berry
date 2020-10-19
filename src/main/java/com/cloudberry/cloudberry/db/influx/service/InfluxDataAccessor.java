package com.cloudberry.cloudberry.db.influx.service;

import com.cloudberry.cloudberry.analytics.model.InfluxQueryFields;
import com.cloudberry.cloudberry.db.influx.InfluxDefaults;
import com.cloudberry.cloudberry.db.influx.model.DataFilters;
import com.cloudberry.cloudberry.db.influx.util.FluxQueryUtil;
import com.cloudberry.cloudberry.db.influx.util.RestrictionsFactory;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.exceptions.InfluxException;
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
                .foldRestrictions(
                        Flux.from(bucket).range(Instant.EPOCH),
                        allRestrictions.flatMap(Optional::stream).collect(Collectors.toList())
                )
                .pivot(columnsToKeep, Set.of(InfluxDefaults.Columns.FIELD), InfluxDefaults.Columns.VALUE)
                .drop(InfluxDefaults.EXCLUDED_COLUMNS);

        var rawQuery = query.toString();
        try {
            return influxClient.getQueryApi()
                    .query(rawQuery)
                    .stream()
                    .map(FluxTable::getRecords)
                    .flatMap(List::stream)
                    .peek(record -> record.getValues().keySet().removeAll(InfluxDefaults.EXCLUDED_COLUMNS))
                    .collect(Collectors.toList());
        } catch (InfluxException e) {
            log.warn("There is a problem '{}' with the query: {}", e.getMessage(), rawQuery);
        } catch (Throwable e) {
            log.warn("Unknown problem occurred '{}' with the query: {}", e.getMessage(), rawQuery);
        }

        return List.of();
    }
}
