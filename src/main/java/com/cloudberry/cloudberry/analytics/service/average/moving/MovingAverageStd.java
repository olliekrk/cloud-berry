package com.cloudberry.cloudberry.analytics.service.average.moving;

import com.cloudberry.cloudberry.analytics.model.basic.DataSeries;
import com.cloudberry.cloudberry.analytics.model.query.InfluxQueryFields;
import com.cloudberry.cloudberry.analytics.model.time.ChronoInterval;
import com.cloudberry.cloudberry.analytics.util.FluxUtils;
import com.cloudberry.cloudberry.db.influx.InfluxDefaults;
import com.influxdb.client.InfluxDBClient;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovingAverageStd extends MovingAverage {
    private static final String STDDEV_SERIES_NAME = "STDDEV";

    public MovingAverageStd(InfluxDBClient influxClient) {
        super(influxClient);
    }

    @Override
    public DataSeries getTimedMovingSeries(
            String fieldName,
            ChronoInterval intervalTime,
            List<ObjectId> computationsIds,
            InfluxQueryFields influxQueryFields
    ) {
        var bucketName = influxQueryFields.getBucketName();
        var restrictions = getRestrictions(influxQueryFields, computationsIds, fieldName);

        var timeInterval = intervalTime.asInfluxInterval();

        var query = FluxUtils.epochQueryByComputationId(bucketName, restrictions)
                .expression(timedMovingAverageFluxRaw(timeInterval))
                .groupBy(InfluxDefaults.Columns.TIME)
                .stddev()
                .group(); // ungroup

        return queryTimeValueSeries(query, STDDEV_SERIES_NAME, fieldName);
    }

}
