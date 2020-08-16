package com.cloudberry.cloudberry.parsing.service;

import com.cloudberry.cloudberry.parsing.model.age.AgeUploadDetails;
import com.cloudberry.cloudberry.parsing.model.ParsedLogs;
import com.cloudberry.cloudberry.parsing.model.csv.CsvUploadDetails;
import com.cloudberry.cloudberry.parsing.service.age.AgeLogsParser;
import com.cloudberry.cloudberry.parsing.service.csv.CsvLogsParser;
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
public class LogsImporter {
    private final LogsMetadataAppender logsMetadataAppender;
    private final InfluxDataWriter influxDataWriter;
    private final AgeLogsParser ageLogsParser;
    private final CsvLogsParser csvLogsParser;

    /**
     * Import file in AgE-specific format as evaluation data and return id of saved evaluation.
     */
    public ObjectId importAgeFile(File file,
                                  String experimentName,
                                  AgeUploadDetails uploadDetails) throws IOException {
        var parsedData =
                ageLogsParser.parseFile(file, uploadDetails);
        var parsedDataWithMetadata =
                logsMetadataAppender.appendMetadata(parsedData, experimentName);
        influxDataWriter.writePoints(parsedDataWithMetadata.getBucketName(), parsedDataWithMetadata.getPoints());
        return parsedDataWithMetadata.getExperimentEvaluation().getId();
    }

    public ObjectId importCsvFile(File file,
                                  String experimentName,
                                  CsvUploadDetails uploadDetails) throws IOException {
        var parsedData =
                csvLogsParser.parseFile(file, uploadDetails);
        var parsedDataWithMetadata =
                logsMetadataAppender.appendMetadata(
                        parsedData,
                        experimentName,
                        uploadDetails.getConfigurationId(),
                        uploadDetails.getEvaluationId()
                );
        influxDataWriter.writePoints(parsedDataWithMetadata.getBucketName(), parsedDataWithMetadata.getPoints());
        return parsedDataWithMetadata.getExperimentEvaluation().getId();
    }

}
