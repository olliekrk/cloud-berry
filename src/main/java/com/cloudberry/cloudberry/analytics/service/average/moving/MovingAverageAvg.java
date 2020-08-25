package com.cloudberry.cloudberry.analytics.service.average.moving;

import com.cloudberry.cloudberry.analytics.model.DataSeries;
import com.cloudberry.cloudberry.analytics.model.IntervalTime;
import com.cloudberry.cloudberry.analytics.model.OptionalQueryFields;
import com.cloudberry.cloudberry.common.FluxUtils;
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
                                           IntervalTime intervalTime,
                                           List<ObjectId> computationsIds,
                                           OptionalQueryFields optionalQueryFields) {
        var bucketName = optionalQueryFields.getBucketName();
        var restrictions = getRestrictions(optionalQueryFields, computationsIds, fieldName);

        var timeInterval = intervalTime.toTimeInterval();

        var query = FluxUtils.epochQuery(bucketName, restrictions)
                .keep(Set.of(InfluxDefaults.Columns.TIME, InfluxDefaults.Columns.VALUE))
                .expression(timedMovingAverageFluxRaw(timeInterval))
                .keep(Set.of(InfluxDefaults.Columns.TIME, InfluxDefaults.Columns.VALUE));

        return queryTimeValueSeries(query, AVG_SERIES_NAME, fieldName);
    }
}
