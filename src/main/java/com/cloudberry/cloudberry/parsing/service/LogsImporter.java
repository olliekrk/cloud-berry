package com.cloudberry.cloudberry.parsing.service;

import com.cloudberry.cloudberry.db.influx.service.InfluxDataWriter;
import com.cloudberry.cloudberry.parsing.model.age.AgeUploadDetails;
import com.cloudberry.cloudberry.parsing.model.csv.CsvUploadDetails;
import com.cloudberry.cloudberry.parsing.service.age.AgeLogsParser;
import com.cloudberry.cloudberry.parsing.service.csv.CsvLogsParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogsImporter {
    @Value("${influx.measurements.default-measurement-name}")
    private String defaultMeasurementName;
    private final LogsMetadataAppender logsMetadataAppender;
    private final InfluxDataWriter influxDataWriter;
    private final AgeLogsParser ageLogsParser;
    private final CsvLogsParser csvLogsParser;

    /**
     * Import file in AgE-specific format as computation data and return id of saved computation.
     */
    public ObjectId importAgeFile(File file,
                                  String experimentName,
                                  AgeUploadDetails uploadDetails) throws IOException {
        var parsedData =
                ageLogsParser.parseFile(file, uploadDetails, defaultMeasurementName);
        var parsedDataWithMetadata =
                logsMetadataAppender.appendMetadata(parsedData, experimentName);
        influxDataWriter.writePoints(parsedDataWithMetadata.getBucketName(), parsedDataWithMetadata.getPoints());
        return parsedDataWithMetadata.getExperimentComputation().getId();
    }

    public ObjectId importCsvFile(File file,
                                  String experimentName,
                                  CsvUploadDetails uploadDetails) throws IOException {
        var parsedData =
                csvLogsParser.parseFile(file, uploadDetails, defaultMeasurementName);
        var parsedDataWithMetadata =
                logsMetadataAppender.appendMetadata(
                        parsedData,
                        experimentName,
                        uploadDetails.getConfigurationId(),
                        uploadDetails.getComputationId()
                );
        influxDataWriter.writePoints(parsedDataWithMetadata.getBucketName(), parsedDataWithMetadata.getPoints());
        return parsedDataWithMetadata.getExperimentComputation().getId();
    }

}
