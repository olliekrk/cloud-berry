package com.cloudberry.cloudberry.service.api;

import com.cloudberry.cloudberry.db.influx.service.InfluxDataEvictor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BucketsService {
    private final InfluxDataEvictor influxDataEvictor;

    public void deleteBucket(String bucketName) {
        influxDataEvictor.deleteBucket(bucketName);
    }
}
