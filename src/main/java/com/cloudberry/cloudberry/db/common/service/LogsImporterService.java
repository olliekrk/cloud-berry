package com.cloudberry.cloudberry.db.common.service;

import com.cloudberry.cloudberry.db.common.data.ImportDetails;
import com.cloudberry.cloudberry.db.common.data.ParsedLogs;
import com.cloudberry.cloudberry.db.influx.service.InfluxDataWriter;
import com.cloudberry.cloudberry.db.mongo.data.metadata.Experiment;
import com.cloudberry.cloudberry.db.mongo.repository.facades.MetadataRepositoryFacade;
import com.cloudberry.cloudberry.db.mongo.service.MetadataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogsImporterService {
    private final InfluxDataWriter influxDBConnector;
    private final MetadataService metadataService;
    private final MetadataRepositoryFacade metadataRepositoryFacade;
    private final LogsParser<ParsedLogs> logsParser;

    public boolean importExperimentFile(File file,
                                        ImportDetails importDetails,
                                        String experimentName) throws IOException {
        var experiment = metadataService.findExperimentByName(experimentName)
                .orElseGet(() -> metadataService.createExperiment(
                        new Experiment(Instant.now(), experimentName, Collections.emptyMap())
                ));

        var parsedLogs = logsParser.parseExperimentFile(file, experiment.getId(), importDetails);
        influxDBConnector.writePoints(parsedLogs.getBucketName(), parsedLogs.getPoints());
        metadataRepositoryFacade.save(parsedLogs.getExperimentConfiguration()).subscribe();
        metadataRepositoryFacade.save(parsedLogs.getExperimentEvaluation()).subscribe();
        return true;
    }
}
