package com.cloudberry.cloudberry.analytics.service;

import com.cloudberry.cloudberry.analytics.api.MovingAverageApi;
import com.cloudberry.cloudberry.analytics.model.DataSeries;
import com.cloudberry.cloudberry.analytics.model.IntervalTime;
import com.cloudberry.cloudberry.analytics.model.OptionalQueryFields;
import com.cloudberry.cloudberry.common.syntax.ListSyntax;
import com.cloudberry.cloudberry.config.influx.InfluxConfig;
import com.cloudberry.cloudberry.db.influx.InfluxDefaults.Columns;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.query.dsl.Flux;
import com.influxdb.query.dsl.functions.properties.TimeInterval;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MovingAverageSupplier implements MovingAverageApi {
    private final InfluxConfig influxConfig;
    private final InfluxDBClient influxClient;
    private static final String AVG_SERIES_NAME = "AVG";
    private static final String STDDEV_SERIES_NAME = "STDDEV";

    @Override
    public DataSeries timedMovingAvgSeries(String fieldName,
                                           IntervalTime intervalTime,
                                           List<ObjectId> computationsIds, // nie powinno byc gdzies uzyte? todo fix
                                           OptionalQueryFields optionalQueryFields) {
        var bucketName = ApiSupplier.bucketNameOrDefault(optionalQueryFields.getBucketNameOptional(), influxConfig);
        var timeInterval = intervalTime.toTimeInterval();
        var restrictions = ApiSupplier.getRestrictions(fieldName, optionalQueryFields.getMeasurementNameOptional());

        var query = ApiSupplier.epochQuery(bucketName, restrictions)
                .keep(Set.of(Columns.TIME, Columns.VALUE))
                .expression(timedMovingAverageFluxRaw(timeInterval))
                .keep(Set.of(Columns.TIME, Columns.VALUE));

        return queryTimeValueSeries(query, AVG_SERIES_NAME, fieldName);
    }

    @Override
    public DataSeries timedMovingStdSeries(String fieldName,
                                           IntervalTime intervalTime,
                                           List<ObjectId> computationsIds, // nie powinno byc gdzies uzyte? todo fix
                                           OptionalQueryFields optionalQueryFields) {
        var bucketName = ApiSupplier.bucketNameOrDefault(optionalQueryFields.getBucketNameOptional(), influxConfig);
        var timeInterval = intervalTime.toTimeInterval();
        var restrictions = ApiSupplier.getRestrictions(fieldName, optionalQueryFields.getMeasurementNameOptional());

        var query = ApiSupplier.epochQueryByComputationId(bucketName, restrictions)
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
