package com.cloudberry.cloudberry.db.influx.data;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.kafka.common.protocol.types.Field;

import java.time.Instant;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Measurement(name = "workplace_log234")
public class WorkplaceLogMeasurement {

    @Column
    long workplaceId;

    @Column(tag = true)
    String evaluationId;

    @Column(timestamp = true)
    Instant time;

    @Column(tag = true)
    Map<String, String> superMap;

}
