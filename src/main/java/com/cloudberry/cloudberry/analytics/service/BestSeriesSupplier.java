package com.cloudberry.cloudberry.analytics.service;

import com.cloudberry.cloudberry.analytics.api.BestSeriesApi;
import com.cloudberry.cloudberry.analytics.model.DataSeries;
import com.cloudberry.cloudberry.analytics.model.OptimizationGoal;
import com.cloudberry.cloudberry.analytics.model.OptimizationKind;
import com.influxdb.client.InfluxDBClient;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BestSeriesSupplier implements BestSeriesApi {

    private final InfluxDBClient influxClient;

    @Override
    public List<DataSeries> nBestSeriesForField(int n,
                                                String fieldName,
                                                OptimizationGoal optimizationGoal,
                                                OptimizationKind optimizationKind,
                                                @Nullable String bucketName,
                                                @Nullable String measurementName) {
        return null;
    }
}
