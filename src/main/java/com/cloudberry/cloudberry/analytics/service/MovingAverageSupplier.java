package com.cloudberry.cloudberry.analytics.service;

import com.cloudberry.cloudberry.analytics.api.MovingAverageApi;
import com.cloudberry.cloudberry.analytics.model.DataSeries;
import com.cloudberry.cloudberry.common.syntax.ListSyntax;
import com.cloudberry.cloudberry.config.influx.InfluxConfig;
import com.cloudberry.cloudberry.db.influx.InfluxDefaults;
import com.cloudberry.cloudberry.db.influx.InfluxDefaults.Columns;
import com.cloudberry.cloudberry.db.influx.util.RestrictionsFactory;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.dsl.Flux;
import com.influxdb.query.dsl.functions.properties.TimeInterval;
import com.influxdb.query.dsl.functions.restriction.Restrictions;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import javax.validation.constraints.Null;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovingAverageSupplier extends ApiSupplier implements MovingAverageApi {
    private final InfluxConfig influxConfig;
    private final InfluxDBClient influxClient;
    private static final String AVG_SERIES_NAME = "AVG";
    private static final String STDDEV_SERIES_NAME = "STDDEV";

    @Override
    public DataSeries timedMovingAvgSeries(String fieldName,
                                           Long interval,
                                           ChronoUnit chronoUnit,
                                           List<ObjectId> computationsIds,
                                           @Nullable String measurementNameOpt,
                                           @Nullable String bucketNameOpt) {
        var bucketName = bucketNameOrDefault(bucketNameOpt, influxConfig);
        var timeInterval = new TimeInterval(interval, chronoUnit);
        var fieldRestriction = RestrictionsFactory.hasField(fieldName);
        var restrictions = measurementNameOpt == null ? fieldRestriction :
                Restrictions.and(fieldRestriction, RestrictionsFactory.measurement(measurementNameOpt));

        var query = epochQuery(bucketName, restrictions)
                .keep(Set.of(Columns.TIME, Columns.VALUE))
                .expression(timedMovingAverageFluxRaw(timeInterval))
                .keep(Set.of(Columns.TIME, Columns.VALUE));

        return queryTimeValueSeries(query, AVG_SERIES_NAME, fieldName);
    }

    @Override
    public DataSeries timedMovingStdSeries(String fieldName,
                                           Long interval,
                                           ChronoUnit chronoUnit,
                                           List<ObjectId> computationsIds,
                                           @Nullable String measurementNameOpt,
                                           @Nullable String bucketNameOpt) {
        var bucketName = bucketNameOrDefault(bucketNameOpt, influxConfig);
        var timeInterval = new TimeInterval(interval, chronoUnit);
        var fieldRestriction = RestrictionsFactory.hasField(fieldName);
        var restrictions = measurementNameOpt == null ? fieldRestriction :
                Restrictions.and(fieldRestriction, RestrictionsFactory.measurement(measurementNameOpt));

        var query = epochQueryByComputationId(bucketName, restrictions)
                .expression(timedMovingAverageFluxRaw(timeInterval))
                .groupBy(Columns.TIME)
                .stddev()
                .group(); // ungroup

        return queryTimeValueSeries(query, STDDEV_SERIES_NAME, fieldName);
    }

    /**
     * Query the DB & create {{@link DataSeries}} from result having only _time & _value fields.
     */
    private DataSeries queryTimeValueSeries(Flux query,
                                            String seriesName,
                                            String fieldName) {
        return influxClient
                .getQueryApi()
                .query(query.toString())
                .stream()
                .map(table -> {
                    var data = ListSyntax.mapped(
                            table.getRecords(),
                            record -> Map.of(
                                    Columns.TIME, record.getTime(),
                                    fieldName, record.getValue()
                            ));
                    return new DataSeries(seriesName, data);
                })
                .findAny()
                .orElse(DataSeries.empty(seriesName));
    }

    private static String timedMovingAverageFluxRaw(TimeInterval timeInterval) {
        return String.format(
                "timedMovingAverage(every: %s, period: %s)",
                timeInterval.toString(),
                timeInterval.toString()
        );
    }

}
