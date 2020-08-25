package com.cloudberry.cloudberry.service.api;

import com.cloudberry.cloudberry.analytics.model.InfluxQueryFields;
import com.cloudberry.cloudberry.analytics.util.FluxUtils;
import com.cloudberry.cloudberry.common.syntax.ListSyntax;
import com.cloudberry.cloudberry.db.influx.InfluxDefaults;
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
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InfluxUtilService {
    private final InfluxDBClient influxClient;

    /**
     * Computes the best suitable interval duration, based on average series length and number of series.
     * This is later used as parameter e.g in influx's `timedMovingAverage()`
     */
    public long averageIntervalNanos(String fieldName,
                                     List<ObjectId> computationsIds,
                                     InfluxQueryFields influxQueryFields) {
        var bucketName = influxQueryFields.getBucketName();
        var tagRestriction = RestrictionsFactory
                .tagIn(InfluxDefaults.CommonTags.COMPUTATION_ID, ListSyntax.mapped(computationsIds, ObjectId::toHexString));
        var restrictions = influxQueryFields.getMeasurementNameOptional()
                .map(name -> Restrictions.and(RestrictionsFactory.measurement(name), tagRestriction))
                .orElse(tagRestriction);

        var countsQuery = FluxUtils.epochQuery(bucketName, RestrictionsFactory.hasField(fieldName))
                .filter(restrictions)
                .keep(Set.of(InfluxDefaults.CommonTags.COMPUTATION_ID, InfluxDefaults.Columns.VALUE))
                .count()
                .group(); // ungroup

        var timeQuery = FluxUtils.epochQuery(bucketName, RestrictionsFactory.hasField(fieldName))
                .filter(restrictions)
                .keep(Set.of(InfluxDefaults.Columns.TIME, InfluxDefaults.Columns.VALUE))
                .sort(Set.of(InfluxDefaults.Columns.TIME));

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
                .flatMap(FluxUtils::tableToTime)
                .findAny()
                .orElseThrow();

        var max = influxClient
                .getQueryApi()
                .query(timeQuery.last().toString())
                .stream()
                .flatMap(FluxUtils::tableToTime)
                .findAny()
                .orElseThrow();

        return (long) Math.floor(Duration.between(min, max).toNanos() / Math.max(1, counts));
    }
}
