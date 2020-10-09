package com.cloudberry.cloudberry.service.api;

import com.cloudberry.cloudberry.common.syntax.ListSyntax;
import com.cloudberry.cloudberry.config.influx.InfluxConfig;
import com.cloudberry.cloudberry.db.influx.service.InfluxOrganizationService;
import com.influxdb.client.BucketsApi;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.domain.Bucket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BucketsService {
    private final InfluxOrganizationService influxOrganizationService;
    private final InfluxDBClient influxClient;

    public List<String> getBucketNames() {
        var buckets = bucketsApi();
        return ListSyntax.mapped(buckets.findBuckets(), Bucket::getName);
    }

    public String createBucketIfNotExists(String bucketName) {
        var buckets = bucketsApi();
        return findBucketByName(bucketName).orElseGet(() -> {
            log.info("Creating new bucket: " + bucketName);
            return buckets.createBucket(bucketName, influxOrganizationService.getDefaultOrganizationId());
        }).getName();
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
