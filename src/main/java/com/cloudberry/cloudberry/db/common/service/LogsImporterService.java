package com.cloudberry.cloudberry.db.common.service;

import com.cloudberry.cloudberry.db.common.data.ImportDetails;
import com.cloudberry.cloudberry.db.common.data.ParsedLogs;
import com.cloudberry.cloudberry.db.influx.service.InfluxDataWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogsImporterService {
    private final InfluxDataWriter influxDBConnector;
    private final AgeLogsParser<ParsedLogs> logsParser;

    public void importExperimentFile(File file,
                                     ImportDetails importDetails,
                                     String experimentName) throws IOException {
        var parsedLogs = logsParser.parseExperimentFile(file, experimentName, importDetails);
        influxDBConnector.writePoints(parsedLogs.getBucketName(), parsedLogs.getPoints());
    }
}
