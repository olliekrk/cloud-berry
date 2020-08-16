package com.cloudberry.cloudberry.parsing.service;

import com.cloudberry.cloudberry.parsing.model.ParsedLogsWithMetadata;
import com.cloudberry.cloudberry.parsing.model.ParsedLogs;
import com.cloudberry.cloudberry.db.influx.InfluxDefaults;
import com.cloudberry.cloudberry.db.mongo.data.metadata.Experiment;
import com.cloudberry.cloudberry.db.mongo.data.metadata.ExperimentConfiguration;
import com.cloudberry.cloudberry.db.mongo.data.metadata.ExperimentComputation;
import com.cloudberry.cloudberry.db.mongo.service.MetadataService;
import com.cloudberry.cloudberry.parsing.model.age.AgeParsedLogs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogsMetadataAppender {
    @Value("${influx.buckets.default-logs}")
    private String defaultLogsBucketName;
    private final MetadataService metadataService;

    public ParsedLogsWithMetadata appendMetadata(ParsedLogs parsedLogs,
                                                 String experimentName,
                                                 ObjectId configurationId,
                                                 ObjectId computationId) {
        var now = Instant.now();
        var experiment = metadataService
                .getOrCreateExperiment(new Experiment(new ObjectId(), experimentName, Map.of(), now))
                .block();
        var configuration = metadataService
                .getOrCreateConfiguration(new ExperimentConfiguration(configurationId, experiment.getId(), null, Map.of(), now))
                .block();
        var computation = metadataService
                .getOrCreateComputation(new ExperimentComputation(computationId, configuration.getId(), now))
                .block();
        
        return new ParsedLogsWithMetadata(defaultLogsBucketName, parsedLogs.getPoints(), configuration, computation);
    }

    public ParsedLogsWithMetadata appendMetadata(AgeParsedLogs parsedLogs, String experimentName) {
        var now = Instant.now();
        var experiment = metadataService
                .getOrCreateExperiment(new Experiment(new ObjectId(), experimentName, Map.of(), now))
                .block();
        var configuration = metadataService
                .getOrCreateConfiguration(
                        new ExperimentConfiguration(
                                new ObjectId(),
                                experiment.getId(),
                                parsedLogs.getConfigurationName(),
                                parsedLogs.getConfigurationParameters(),
                                now))
                .block();
        var computation = metadataService
                .getOrCreateComputation(new ExperimentComputation(new ObjectId(), configuration.getId(), now))
                .block();
        parsedLogs
                .getPoints()
                .forEach(point -> point.addTag(InfluxDefaults.CommonTags.COMPUTATION_ID, computation.getId().toHexString()));

        return new ParsedLogsWithMetadata(defaultLogsBucketName, parsedLogs.getPoints(), configuration, computation);
    }

    private Mono<Experiment> getOrCreateExperiment(String experimentName, Instant time) {
        return metadataService.getOrCreateExperiment(
                new Experiment(
                        new ObjectId(),
                        experimentName,
                        Map.of(),
                        time)
        );
    }

}
