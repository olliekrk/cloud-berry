package com.cloudberry.cloudberry.db.influx.service;

import com.cloudberry.cloudberry.service.RawLogsHandler;
import com.cloudberry.cloudberry.service.LogsParser;
import com.influxdb.client.write.Point;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Profile("influx")
public class RawLogsToInflux implements RawLogsHandler {
    private final InfluxMeasurementWriter influxDBConnector;
    private final LogsParser<Point> logsParser;

    public boolean saveLogsToDatabase(String rawLogs) {
        List<Point> points = logsParser.parseMeasurements(rawLogs);
        influxDBConnector.writePoints(points);
        return true;
    }
}
