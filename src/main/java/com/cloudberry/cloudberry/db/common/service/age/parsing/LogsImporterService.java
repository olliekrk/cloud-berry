package com.cloudberry.cloudberry.db.common.service.age.parsing;

import com.cloudberry.cloudberry.db.common.data.ImportDetails;
import com.cloudberry.cloudberry.db.influx.service.InfluxDataWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogsImporterService {
    private final InfluxDataWriter influxDataWriter;
    private final LogsParser logsParser;

    /**
     * Import file in AgE-specific format as evaluation data and return id of saved evaluation.
     */
    public ObjectId importAgeFile(File file,
                                  ImportDetails importDetails,
                                  String experimentName) throws IOException {
        var parsedLogs = logsParser.parseLogs(file, experimentName, importDetails);
        influxDataWriter.writePoints(parsedLogs.getBucketName(), parsedLogs.getPoints());
        return parsedLogs.getExperimentEvaluation().getId();
    }

    public void importCsvFile(File file,
                              String experimentName) {
        // todo
    }
}
