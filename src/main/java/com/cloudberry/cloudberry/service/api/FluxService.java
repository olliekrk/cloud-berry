package com.cloudberry.cloudberry.service.api;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.domain.Query;
import com.influxdb.query.FluxTable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FluxService {
    private final InfluxDBClient influxClient;

    public List<FluxTable> doQuery(String rawQuery) {
        var fluxQuery = new Query().query(rawQuery);
        return influxClient.getQueryApi().query(fluxQuery);
    }

}
