package com.cloudberry.cloudberry.service.api;

import com.cloudberry.cloudberry.analytics.model.basic.DataSeries;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.query.FluxRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FluxService {
    public static final String FLUX_SERIES_NAME = "flux_query";
    private final InfluxDBClient influxClient;

    public DataSeries queryForSeries(String rawQuery) {
        var rawQueryData = influxClient.getQueryApi()
                .query(rawQuery)
                .stream()
                .flatMap(table -> table.getRecords().stream())
                .map(FluxRecord::getValues)
                .collect(Collectors.toList());

        return new DataSeries(FLUX_SERIES_NAME, rawQueryData);
    }

}
