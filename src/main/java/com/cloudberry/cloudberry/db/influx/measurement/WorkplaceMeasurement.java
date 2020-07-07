package com.cloudberry.cloudberry.db.influx.measurement;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.time.Instant;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Measurement(name = "workplace_log")
public class WorkplaceMeasurement {

    @Column
    long workplaceId;

    @Column(tag = true)
    ObjectId evaluationId;

    @Column(timestamp = true)
    Instant time;

    @Column(tag = true)
    Map<String, String> parameters;

}
