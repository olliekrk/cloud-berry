package com.cloudberry.cloudberry.influx2.measurement;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import lombok.Data;

import java.time.Instant;

@Data
@Measurement(name = "workplace_log")
public class WorkplaceLogMeasurement {

    @Column
    String workplaceId;

    @Column(tag = true)
    String evaluationId;

    @Column(timestamp = true)
    Instant time;

}
