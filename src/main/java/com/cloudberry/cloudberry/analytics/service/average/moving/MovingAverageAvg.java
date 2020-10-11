package com.cloudberry.cloudberry.analytics.service.average.moving;

import com.cloudberry.cloudberry.analytics.model.DataSeries;
import com.cloudberry.cloudberry.analytics.model.InfluxQueryFields;
import com.cloudberry.cloudberry.analytics.model.time.ChronoInterval;
import com.cloudberry.cloudberry.analytics.util.FluxUtils;
import com.cloudberry.cloudberry.db.influx.InfluxDefaults;
import com.influxdb.client.InfluxDBClient;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class MovingAverageAvg extends MovingAverage {
    private static final String AVG_SERIES_NAME = "AVG";

    public MovingAverageAvg(InfluxDBClient influxClient) {
        super(influxClient);
    }

    @Override
    public DataSeries getTimedMovingSeries(String fieldName,
                                           ChronoInterval intervalTime,
                                           List<ObjectId> computationsIds,
                                           InfluxQueryFields influxQueryFields) {
        var bucketName = influxQueryFields.getBucketName();
        var restrictions = getRestrictions(influxQueryFields, computationsIds, fieldName);

        var timeInterval = intervalTime.asInfluxInterval();

        var query = FluxUtils.epochQuery(bucketName, restrictions)
                .keep(Set.of(InfluxDefaults.Columns.TIME, InfluxDefaults.Columns.VALUE))
                .expression(timedMovingAverageFluxRaw(timeInterval))
                .keep(Set.of(InfluxDefaults.Columns.TIME, InfluxDefaults.Columns.VALUE));

        return queryTimeValueSeries(query, AVG_SERIES_NAME, fieldName);
    }
}
