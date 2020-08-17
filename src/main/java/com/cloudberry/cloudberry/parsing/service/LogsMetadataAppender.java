package com.cloudberry.cloudberry.parsing.service;

import com.cloudberry.cloudberry.db.influx.InfluxDefaults;
import com.cloudberry.cloudberry.db.mongo.data.metadata.Experiment;
import com.cloudberry.cloudberry.db.mongo.data.metadata.ExperimentComputation;
import com.cloudberry.cloudberry.db.mongo.data.metadata.ExperimentConfiguration;
import com.cloudberry.cloudberry.db.mongo.service.MetadataService;
import com.cloudberry.cloudberry.parsing.model.ParsedLogs;
import com.cloudberry.cloudberry.parsing.model.ParsedLogsWithMetadata;
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
        var experiment = getOrCreateExperiment(experimentName, now)
                .block();

        var configuration = getOrCreateConfiguration(null, Map.of(), experiment.getId(), now, configurationId)
                .block();

        var computation = getOrCreateComputation(configurationId, now, computationId)
                .block();

        return new ParsedLogsWithMetadata(defaultLogsBucketName, parsedLogs.getPoints(), configuration, computation);
    }

    public ParsedLogsWithMetadata appendMetadata(AgeParsedLogs parsedLogs, String experimentName) {
        var now = Instant.now();
        var experiment = getOrCreateExperiment(experimentName, now)
                .block();

        var configuration = getOrCreateConfiguration(
                parsedLogs.getConfigurationName(),
                parsedLogs.getConfigurationParameters(),
                experiment.getId(),
                now
        ).block();

        var computation = getOrCreateComputation(configuration.getId(), now)
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
                        time
                )
        );
    }

    private Mono<ExperimentComputation> getOrCreateComputation(ObjectId configurationId, Instant time, ObjectId computationId) {
        return metadataService.getOrCreateComputation(
                new ExperimentComputation(
                        computationId,
                        configurationId,
                        time
                )
        );
    }

    private Mono<ExperimentComputation> getOrCreateComputation(ObjectId configurationId, Instant time) {
        return getOrCreateComputation(configurationId, time, new ObjectId());
    }

    private Mono<ExperimentConfiguration> getOrCreateConfiguration(
            String configurationFilename,
            Map<String, Object> parameters,
            ObjectId experimentId,
            Instant time,
            ObjectId configurationId
    ) {
        return metadataService.getOrCreateConfiguration(
                new ExperimentConfiguration(
                        configurationId,
                        experimentId,
                        configurationFilename,
                        parameters,
                        time
                )
        );
    }

    private Mono<ExperimentConfiguration> getOrCreateConfiguration(
            String configurationFilename,
            Map<String, Object> parameters,
            ObjectId experimentId,
            Instant time
    ) {
        return getOrCreateConfiguration(configurationFilename, parameters, experimentId, time, new ObjectId());
    }
}
