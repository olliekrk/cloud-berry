package com.cloudberry.cloudberry.parsing.service;

import com.cloudberry.cloudberry.config.influx.InfluxConfig;
import com.cloudberry.cloudberry.db.influx.service.InfluxDataWriter;
import com.cloudberry.cloudberry.parsing.model.age.AgeUploadDetails;
import com.cloudberry.cloudberry.parsing.model.csv.CsvUploadDetails;
import com.cloudberry.cloudberry.parsing.service.age.AgeLogsParser;
import com.cloudberry.cloudberry.parsing.service.csv.CsvLogsParser;
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
    private final InfluxConfig influxConfig;
    private final LogsMetadataAppender logsMetadataAppender;
    private final InfluxDataWriter influxDataWriter;
    private final AgeLogsParser ageLogsParser;
    private final CsvLogsParser csvLogsParser;

    /**
     * Import file in AgE-specific format as computation data and return id of saved computation.
     */
    public ObjectId importAgeFile(
            File file,
            String experimentName,
            AgeUploadDetails uploadDetails
    ) throws IOException {
        final var parsedData =
                ageLogsParser.parseFile(file, uploadDetails, influxConfig.getDefaultMeasurementName());
        final var parsedDataWithMetadata =
                logsMetadataAppender.appendMetadata(parsedData, experimentName);
        influxDataWriter.writePoints(parsedDataWithMetadata.getBucketName(), parsedDataWithMetadata.getPoints());
        return parsedDataWithMetadata.getExperimentComputation().getId();
    }

    public ObjectId importCsvFile(
            File file,
            String experimentName,
            CsvUploadDetails uploadDetails
    ) throws IOException {
        final var parsedData =
                csvLogsParser.parseFile(file, uploadDetails, influxConfig.getDefaultMeasurementName());
        final var parsedDataWithMetadata =
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
