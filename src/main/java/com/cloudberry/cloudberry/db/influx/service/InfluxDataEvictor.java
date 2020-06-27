package com.cloudberry.cloudberry.db.influx.service;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.domain.DeletePredicateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

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
        influxClient.getDeleteApi().delete(
                new DeletePredicateRequest().predicate(String.format("_measurement=\"%s\"", bucket)),
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
