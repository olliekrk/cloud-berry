package com.cloudberry.cloudberry.service.api;

import com.cloudberry.cloudberry.analytics.model.DataSeries;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.domain.Query;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FluxService {
    private final InfluxDBClient influxClient;

    public DataSeries queryForSeries(String rawQuery) {
        var rawQueryData = influxClient.getQueryApi()
                .query(rawQuery)
                .stream()
                .flatMap(table -> table.getRecords().stream())
                .map(FluxRecord::getValues)
                .collect(Collectors.toList());

        return new DataSeries(rawQuery, rawQueryData);
    }

}
