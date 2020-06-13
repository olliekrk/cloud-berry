package com.cloudberry.cloudberry.db.influx;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InfluxDBConnector {

    private final static WritePrecision DEFAULT_PRECISION = WritePrecision.MS;
    private final InfluxDBClient influxClient;

    public <M> void writeMeasurement(M measurement) {
        try (var writeApi = influxClient.getWriteApi()) {
            writeApi.writeMeasurement(DEFAULT_PRECISION, measurement);
        }
    }

    public <M> void writeMeasurements(Collection<M> measurements) {
        try (var writeApi = influxClient.getWriteApi()) {
            writeApi.writeMeasurements(DEFAULT_PRECISION, List.copyOf(measurements));
        }
    }

    public void writePoint(Point point) {
        try (var writeApi = influxClient.getWriteApi()) {
            writeApi.writePoint(point);
        }
    }

    public void writePoints(Collection<Point> points) {
        try (var writeApi = influxClient.getWriteApi()) {
            writeApi.writePoints(List.copyOf(points));
        }
    }

}
