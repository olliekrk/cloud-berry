package com.cloudberry.cloudberry.db.influx.data;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Measurement(name = "workplace_log")
public class WorkplaceLogMeasurement {

    @Column
    long workplaceId;

    @Column(tag = true)
    String evaluationId;

    @Column(timestamp = true)
    Instant time;

}
