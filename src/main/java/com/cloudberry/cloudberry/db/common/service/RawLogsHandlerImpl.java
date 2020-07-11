package com.cloudberry.cloudberry.db.common.service;

import com.cloudberry.cloudberry.db.common.data.ParsedLogs;
import com.cloudberry.cloudberry.db.influx.service.InfluxDataWriter;
import com.cloudberry.cloudberry.db.mongo.repository.facades.MetadataRepositoryFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RawLogsHandlerImpl implements RawLogsHandler {
    private final InfluxDataWriter influxDBConnector;
    private final MetadataRepositoryFacade metadataRepositoryFacade;
    private final LogsParser<ParsedLogs> logsParser;

    public boolean saveLogsToDatabase(String rawLogs) {
        List<ParsedLogs> parsedLogs = logsParser.parseMeasurements(rawLogs);
        parsedLogs.forEach(parsedLog -> {
            influxDBConnector.writePoints(parsedLog.getBucketName(), parsedLog.getPoints());
            metadataRepositoryFacade.save(parsedLog.getExperimentConfiguration()).subscribe();
            metadataRepositoryFacade.save(parsedLog.getExperimentEvaluation()).subscribe();
        });
        return true;
    }
}
