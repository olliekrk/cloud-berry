package com.cloudberry.cloudberry.db.common.service;

import com.cloudberry.cloudberry.db.common.data.ImportDetails;
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
    private final InfluxDataWriter influxDataWriter;
    private final AgeLogsParserService ageLogsParser;

    public void importAgeFile(File file,
                              ImportDetails importDetails,
                              String experimentName) throws IOException {
        var parsedLogs = ageLogsParser.parseFile(file, experimentName, importDetails);
        influxDataWriter.writePoints(parsedLogs.getBucketName(), parsedLogs.getPoints());
    }

    public void importCsvFile(File file,
                              String experimentName) {
        // todo
    }
}
