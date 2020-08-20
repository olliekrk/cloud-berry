package com.cloudberry.cloudberry.analytics.service;

import com.cloudberry.cloudberry.analytics.api.SeriesApi;
import com.cloudberry.cloudberry.analytics.model.DataSeries;
import com.cloudberry.cloudberry.analytics.model.OptionalQueryFields;
import com.cloudberry.cloudberry.common.syntax.ListSyntax;
import com.cloudberry.cloudberry.config.influx.InfluxConfig;
import com.cloudberry.cloudberry.db.influx.InfluxDefaults;
import com.cloudberry.cloudberry.db.influx.InfluxDefaults.Columns;
import com.cloudberry.cloudberry.db.influx.InfluxDefaults.CommonTags;
import com.cloudberry.cloudberry.db.influx.util.RestrictionsFactory;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.dsl.functions.restriction.Restrictions;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SeriesSupplier implements SeriesApi {
    private final InfluxConfig influxConfig;
    private final InfluxDBClient influxClient;

    @Override
    public List<DataSeries> computationsSeries(String fieldName,
                                               List<ObjectId> computationsIds,
                                               OptionalQueryFields optionalQueryFields) {
        var bucketName = ApiSupplier.bucketNameOrDefault(optionalQueryFields.getBucketNameOptional(), influxConfig);
        var fieldRestriction = RestrictionsFactory.hasField(fieldName);
        var tagRestriction = RestrictionsFactory
                .tagIn(CommonTags.COMPUTATION_ID, ListSyntax.mapped(computationsIds, ObjectId::toHexString));
        final var necessaryRestrictions = Restrictions.and(fieldRestriction, tagRestriction);
        var restrictions = optionalQueryFields.getMeasurementNameOptional()
                .map(name -> Restrictions.and(RestrictionsFactory.measurement(name), necessaryRestrictions))
                .orElse(necessaryRestrictions);

        var query = ApiSupplier.epochQuery(bucketName, restrictions)
                .pivot(
                        Set.of(CommonTags.COMPUTATION_ID, Columns.TIME),
                        Set.of(Columns.FIELD),
                        Columns.VALUE
                )
                .drop(InfluxDefaults.EXCLUDED_COLUMNS);

        return influxClient
                .getQueryApi()
                .query(query.toString())
                .stream()
                .map(ApiSupplier::tableToComputationSeries)
                .flatMap(Optional::stream)
                .collect(Collectors.toList());
    }

    @Override
    public long averageIntervalNanos(String fieldName,
                                     List<ObjectId> computationsIds,
                                     OptionalQueryFields optionalQueryFields) {
        var bucketName = ApiSupplier.bucketNameOrDefault(optionalQueryFields.getBucketNameOptional(), influxConfig);
        var tagRestriction = RestrictionsFactory
                .tagIn(CommonTags.COMPUTATION_ID, ListSyntax.mapped(computationsIds, ObjectId::toHexString));
        var restrictions = optionalQueryFields.getMeasurementNameOptional()
                .map(name -> Restrictions.and(RestrictionsFactory.measurement(name), tagRestriction))
                .orElse(tagRestriction);

        var countsQuery = ApiSupplier.epochQuery(bucketName, RestrictionsFactory.hasField(fieldName))
                .filter(restrictions)
                .keep(Set.of(CommonTags.COMPUTATION_ID, Columns.VALUE))
                .count()
                .group(); // ungroup

        var timeQuery = ApiSupplier.epochQuery(bucketName, RestrictionsFactory.hasField(fieldName))
                .filter(restrictions)
                .keep(Set.of(Columns.TIME, Columns.VALUE))
                .sort(Set.of(Columns.TIME));

        var counts = influxClient
                .getQueryApi()
                .query(countsQuery.toString())
                .stream()
                .flatMap(table -> table.getRecords().stream())
                .map(FluxRecord::getValue)
                .filter(Objects::nonNull)
                .map(obj -> Long.parseLong(obj.toString()))
                .collect(Collectors.averagingLong(Long::longValue));

        var min = influxClient
                .getQueryApi()
                .query(timeQuery.first().toString())
                .stream()
                .flatMap(ApiSupplier::tableToTime)
                .findAny()
                .orElseThrow();

        var max = influxClient
                .getQueryApi()
                .query(timeQuery.last().toString())
                .stream()
                .flatMap(ApiSupplier::tableToTime)
                .findAny()
                .orElseThrow();

        return (long) Math.floor(Duration.between(min, max).toNanos() / Math.max(1, counts));
    }

}
