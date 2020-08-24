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

@Service
public class MovingAverageStd extends MovingAverage {
    private static final String STDDEV_SERIES_NAME = "STDDEV";

    public MovingAverageStd(InfluxDBClient influxClient) {
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

        var query = FluxUtils.epochQueryByComputationId(bucketName, restrictions)
                .expression(timedMovingAverageFluxRaw(timeInterval))
                .groupBy(InfluxDefaults.Columns.TIME)
                .stddev()
                .group(); // ungroup

        return queryTimeValueSeries(query, STDDEV_SERIES_NAME, fieldName);
    }

}
