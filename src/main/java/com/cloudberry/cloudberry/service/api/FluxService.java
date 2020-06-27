package com.cloudberry.cloudberry.service.api;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.domain.Query;
import com.influxdb.query.dsl.Flux;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FluxService {
    private final InfluxDBClient influxClient;

    public String doQuery(String rawQuery) {
        var fluxQuery = new Query().query(rawQuery);
        return influxClient.getQueryApi().queryRaw(fluxQuery);
    }

}
