package com.cloudberry.cloudberry.db.influx.service;

import com.cloudberry.cloudberry.db.influx.InfluxDefaults;
import com.cloudberry.cloudberry.service.api.BucketsService;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.write.Point;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class InfluxDataWriter {
    @Value("${influx.buckets.default-logs}")
    private String defaultLogsBucketName;
    @Value("${spring.influx2.org}")
    private String defaultOrganization;

    private final BucketsService bucketsService;
    private final InfluxDBClient influxClient;

    public <M> void writeMeasurement(M measurement) {
        Try.withResources(influxClient::getWriteApi)
                .of(writeApi -> {
                    writeApi.writeMeasurement(InfluxDefaults.WRITE_PRECISION, measurement);
                    return null;
                }).get();
    }

    public <M> void writeMeasurements(Collection<M> measurements) {
        Try.withResources(influxClient::getWriteApi)
                .of(writeApi -> {
                    writeApi.writeMeasurements(InfluxDefaults.WRITE_PRECISION, List.copyOf(measurements));
                    return null;
                }).get();
    }

    public void writePoint(@Nullable String bucketName, Point point) {
        writePoints(bucketName, List.of(point));
    }

    public void writePoints(@Nullable String bucketName, Collection<Point> points) {
        Try.withResources(influxClient::getWriteApi)
                .of(writeApi -> {
                    var bucket = Optional.ofNullable(bucketName).orElse(defaultLogsBucketName);
                    bucketsService.createBucket(bucket);
                    writeApi.writePoints(bucket, defaultOrganization, List.copyOf(points));
                    return null;
                }).get();
    }

}
