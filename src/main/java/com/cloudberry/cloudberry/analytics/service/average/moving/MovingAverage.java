package com.cloudberry.cloudberry.analytics.service.average.moving;

import com.cloudberry.cloudberry.analytics.api.MovingAverageApi;
import com.cloudberry.cloudberry.analytics.model.basic.DataSeries;
import com.cloudberry.cloudberry.analytics.model.basic.SeriesInfo;
import com.cloudberry.cloudberry.analytics.model.query.InfluxQueryFields;
import com.cloudberry.cloudberry.analytics.util.computation.ComputationsRestrictionsFactory;
import com.cloudberry.cloudberry.common.syntax.CollectionSyntax;
import com.cloudberry.cloudberry.common.syntax.ListSyntax;
import com.cloudberry.cloudberry.db.influx.InfluxDefaults;
import com.cloudberry.cloudberry.db.influx.util.RestrictionsFactory;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.query.dsl.Flux;
import com.influxdb.query.dsl.functions.properties.TimeInterval;
import com.influxdb.query.dsl.functions.restriction.Restrictions;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
public abstract class MovingAverage implements MovingAverageApi {
    public static final String AVG_SERIES_NAME = "AVG";
    private final InfluxDBClient influxClient;

    static String timedMovingAverageFluxRaw(TimeInterval timeInterval) {
        return String.format(
                "timedMovingAverage(every: %s, period: %s)",
                timeInterval.toString(),
                timeInterval.toString()
        );
    }

    /**
     * Query the DB & create {{@link DataSeries}} from result having only _time & _value fields.
     */
    protected DataSeries queryTimeValueSeries(
            Flux query,
            SeriesInfo seriesName,
            String fieldName
    ) {
        return influxClient
                .getQueryApi()
                .query(query.toString())
                .stream()
                .map(table -> {
                    var data = ListSyntax.mapped(
                            table.getRecords(),
                            record -> Map.of(
                                    InfluxDefaults.Columns.TIME, record.getTime(),
                                    fieldName, record.getValue()
                            )
                    );
                    return new DataSeries(seriesName, data);
                })
                .findAny()
                .orElse(DataSeries.empty(seriesName));
    }

    protected Restrictions getRestrictions(
            InfluxQueryFields influxQueryFields,
            List<ObjectId> computationsIds,
            String fieldName
    ) {
        return RestrictionsFactory.everyRestriction(CollectionSyntax.flatten(List.of(
                influxQueryFields.getMeasurementNameOptional().map(RestrictionsFactory::measurement),
                Optional.of(fieldName).map(RestrictionsFactory::hasField),
                Optional.of(computationsIds).map(ComputationsRestrictionsFactory::computationIdIn)
        )));
    }
}
