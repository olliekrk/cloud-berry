package com.cloudberry.cloudberry.db.influx.service;

import com.cloudberry.cloudberry.db.influx.InfluxDefaults;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.write.Point;
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
    @Value("${influx.buckets.default-logs-meta}")
    private String defaultLogsMetaBucketName;
    @Value("${spring.influx2.org}")
    private String defaultOrganization;

    private final InfluxDBClient influxClient;

    public <M> void writeMeasurement(M measurement) {
        try (var writeApi = influxClient.getWriteApi()) {
            writeApi.writeMeasurement(InfluxDefaults.WRITE_PRECISION, measurement);
        }
    }

    public <M> void writeMeasurements(Collection<M> measurements) {
        try (var writeApi = influxClient.getWriteApi()) {
            writeApi.writeMeasurements(InfluxDefaults.WRITE_PRECISION, List.copyOf(measurements));
        }
    }

    public void writePoint(@Nullable String bucketName, Point point) {
        writePoints(bucketName, List.of(point));
    }

    public void writePoints(@Nullable String bucketName, Collection<Point> points) {
        try (var writeApi = influxClient.getWriteApi()) {
            var bucket = Optional.ofNullable(bucketName).orElse(defaultLogsBucketName);
            createBucketIfAbsent(bucket);
            writeApi.writePoints(bucket, defaultOrganization, List.copyOf(points));
        }
    }

    private void createBucketIfAbsent(String bucketName) {
        var bucketsApi = influxClient.getBucketsApi();
        var existingBucket = bucketsApi.findBuckets()
                .stream()
                .filter(bucket -> bucket.getName().equals(bucketName))
                .findAny();

        if (existingBucket.isEmpty()) {
            log.info("Creating new bucket: " + bucketName);
            bucketsApi.createBucket(bucketName, defaultOrganization);
        }
    }

}
