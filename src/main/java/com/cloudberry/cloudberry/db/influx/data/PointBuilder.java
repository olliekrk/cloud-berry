package com.cloudberry.cloudberry.db.influx.data;

import com.cloudberry.cloudberry.db.influx.InfluxDefaults;
import com.influxdb.client.write.Point;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;

@Component
public class PointBuilder {

    public Point buildPoint(
            String measurementName,
            Instant time,
            Map<String, Object> fields,
            Map<String, String> tags
    ) {
        return Point.measurement(measurementName)
                .time(time, InfluxDefaults.WRITE_PRECISION)
                .addFields(fields)
                .addTags(tags);
    }

}
