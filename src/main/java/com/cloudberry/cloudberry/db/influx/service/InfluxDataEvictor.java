package com.cloudberry.cloudberry.db.influx.service;

import com.influxdb.client.InfluxDBClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class InfluxDataEvictor {
    @Value("${influx.buckets.default-logs}")
    private String defaultBucketName;
    @Value("${spring.influx2.org}")
    private String defaultOrganization;

    private final InfluxDBClient influxClient;

    public void deleteComputationLogs(@Nullable String bucketName, String measurementName) {
        var bucket = Optional.ofNullable(bucketName).orElse(defaultBucketName);
        var start = OffsetDateTime.ofInstant(Instant.EPOCH, ZoneId.systemDefault()).truncatedTo(ChronoUnit.SECONDS).withOffsetSameLocal(ZoneOffset.UTC);
        var stop = OffsetDateTime.now().truncatedTo(ChronoUnit.SECONDS).withOffsetSameLocal(ZoneOffset.UTC);
        influxClient.getDeleteApi().delete(
                start,
                stop,
                "_measurement=\"" + measurementName + "\"",
                bucket,
                defaultOrganization
        );
    }

    public void deleteBucket(@NonNull String bucketName) {
        influxClient.getBucketsApi()
                .findBuckets()
                .stream().filter(bucket -> bucket.getName().equals(bucketName))
                .findAny()
                .ifPresent(bucket -> influxClient.getBucketsApi().deleteBucket(bucket.getId()));
    }

}
