package com.cloudberry.cloudberry.db.influx.service;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.write.Point;
import com.influxdb.query.dsl.Flux;
import com.influxdb.query.dsl.functions.restriction.Restrictions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class InfluxDataAccessor {
    @Value("${influx.buckets.default-logs}")
    private String defaultBucketName;

    private final InfluxDBClient influxClient;

    // todo
    public List<Point> getComputationLogs(@Nullable String bucketName,
                                          String measurementName,
                                          Map<String, Object> fields,
                                          Map<String, Object> tags) {
        var bucket = Optional.ofNullable(bucketName).orElse(defaultBucketName);
        var api = influxClient.getQueryApi();

        var measurementRestriction = Restrictions.measurement().equal(measurementName);
        var tagRestrictions = tags
                .entrySet()
                .stream()
                .map(entry -> Restrictions.tag(entry.getKey()).equal(entry.getValue()))
                .collect(Collectors.toList());

        tagRestrictions.add(measurementRestriction);

        var query = Flux.from(bucket)
                .range(Instant.EPOCH, Instant.now())
                .filter(Restrictions.and(tagRestrictions.toArray(Restrictions[]::new)))
                .toString();

        return api.query(query, Point.class);
    }
}
