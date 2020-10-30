package com.cloudberry.cloudberry.service.utility;

import com.cloudberry.cloudberry.analytics.model.query.InfluxQueryFields;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InfluxQueryFieldsResolver {
    private final BucketNameResolver bucketNameResolver;

    public InfluxQueryFields get(@Nullable String measurementName, @Nullable String bucketName) {
        return new InfluxQueryFields(measurementName, bucketNameResolver.getOrDefault(bucketName));
    }
}
