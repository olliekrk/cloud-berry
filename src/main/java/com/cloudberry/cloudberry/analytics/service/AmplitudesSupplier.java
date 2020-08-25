package com.cloudberry.cloudberry.analytics.service;

import com.cloudberry.cloudberry.analytics.api.AmplitudesApi;
import com.cloudberry.cloudberry.analytics.model.CriteriaMode;
import com.cloudberry.cloudberry.analytics.model.DataSeries;
import com.cloudberry.cloudberry.analytics.model.InfluxQueryFields;
import com.influxdb.client.InfluxDBClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AmplitudesSupplier implements AmplitudesApi {
    private final InfluxDBClient influxClient;

    @Override
    public List<DataSeries> nLowestAmplitudeSeries(int n, String fieldName, CriteriaMode mode, InfluxQueryFields influxQueryFields) {
        return null;
    }

    @Override
    public List<DataSeries> nHighestAmplitudeSeries(int n, String fieldName, CriteriaMode mode, InfluxQueryFields influxQueryFields) {
        return null;
    }

}
