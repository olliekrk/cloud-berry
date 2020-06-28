package com.cloudberry.cloudberry.db.influx.service;

import com.cloudberry.cloudberry.db.influx.data.ParsedLogs;
import com.cloudberry.cloudberry.service.LogsParser;
import com.cloudberry.cloudberry.service.RawLogsHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Profile("influx")
public class InfluxRawLogsHandler implements RawLogsHandler {
    private final InfluxDataWriter influxDBConnector;
    private final LogsParser<ParsedLogs> logsParser;

    public boolean saveLogsToDatabase(String rawLogs) {
        List<ParsedLogs> parsedLogs = logsParser.parseMeasurements(rawLogs);
        parsedLogs.forEach(parsedLog -> influxDBConnector
                .writePoints(parsedLog.getBucketName(), parsedLog.getPoints()));
        return true;
    }
}
