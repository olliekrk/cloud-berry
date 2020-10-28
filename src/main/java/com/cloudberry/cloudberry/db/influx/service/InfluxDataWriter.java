package com.cloudberry.cloudberry.db.influx.service;

import com.cloudberry.cloudberry.db.influx.InfluxDefaults;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.write.Point;
import com.influxdb.exceptions.InfluxException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class InfluxDataWriter {
    private final InfluxOrganizationService influxOrganizationService;
    private final InfluxPropertiesService influxPropertiesService;
    private final InfluxDBClient influxClient;

    public <M> void writeMeasurement(M measurement) {
        writeMeasurements(List.of(measurement));
    }

    public <M> void writeMeasurements(Collection<M> measurements) {
        try (var writeApi = influxClient.getWriteApi()) {
            writeApi.writeMeasurements(InfluxDefaults.WRITE_PRECISION, List.copyOf(measurements));
        } catch (InfluxException e) {
            log.warn("Writing {} measurements to Influx has failed: {}", measurements.size(), e.getMessage());
        }
    }

    public void writePoint(@Nullable String bucketName, Point point) {
        writePoints(bucketName, List.of(point));
    }

    public void writePoints(@Nullable String bucketName, Collection<Point> points) {
        try (var writeApi = influxClient.getWriteApi()) {
            var bucket = Optional.ofNullable(bucketName).orElse(influxPropertiesService.getDefaultBucketName());
            writeApi.writePoints(bucket, influxOrganizationService.getDefaultOrganizationId(), List.copyOf(points));
        } catch (InfluxException e) {
            log.warn("Writing {} points to Influx has failed: {}", points.size(), e.getMessage());
        }
    }

}
