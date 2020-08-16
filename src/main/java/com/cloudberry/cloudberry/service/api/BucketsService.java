package com.cloudberry.cloudberry.service.api;

import com.cloudberry.cloudberry.util.syntax.ListSyntax;
import com.influxdb.client.BucketsApi;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.domain.Bucket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BucketsService {
    @Value("${spring.influx2.org}")
    private String defaultOrganization;
    private final InfluxDBClient influxClient;

    public List<String> getBucketNames() {
        var buckets = bucketsApi();
        return ListSyntax.mapped(buckets.findBuckets(), Bucket::getName);
    }

    public void createBucket(String bucketName) {
        var buckets = bucketsApi();
        if (findBucketByName(bucketName).isEmpty()) {
            log.info("Creating new bucket: " + bucketName);
            buckets.createBucket(bucketName, defaultOrganization);
        }
    }

    public void deleteBucket(String bucketName) {
        var buckets = bucketsApi();
        Optional.ofNullable(buckets.findBucketByName(bucketName))
                .ifPresent(bucket -> {
                    log.info("Deleting bucket: " + bucketName);
                    buckets.deleteBucket(bucket);
                });
    }

    private Optional<Bucket> findBucketByName(String bucketName) {
        // workaround: BucketsApi.findBucketByName is broken and always returns '_tasks' bucket
        return bucketsApi()
                .findBuckets()
                .stream()
                .filter(bucket -> bucket.getName().equals(bucketName))
                .findFirst();
    }

    private BucketsApi bucketsApi() {
        return this.influxClient.getBucketsApi();
    }

}
