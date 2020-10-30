package com.cloudberry.cloudberry.parsing.service;

import com.cloudberry.cloudberry.db.influx.InfluxDefaults.CommonTags;
import com.cloudberry.cloudberry.db.influx.service.InfluxPropertiesService;
import com.cloudberry.cloudberry.db.mongo.data.metadata.Experiment;
import com.cloudberry.cloudberry.db.mongo.data.metadata.ExperimentComputation;
import com.cloudberry.cloudberry.db.mongo.data.metadata.ExperimentConfiguration;
import com.cloudberry.cloudberry.db.mongo.service.MetadataService;
import com.cloudberry.cloudberry.parsing.model.ParsedLogs;
import com.cloudberry.cloudberry.parsing.model.ParsedLogsWithMetadata;
import com.cloudberry.cloudberry.parsing.model.age.AgeParsedLogs;
import com.influxdb.client.write.Point;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogsMetadataAppender {
    private final InfluxPropertiesService influxPropertiesService;
    private final MetadataService metadataService;

    public ParsedLogsWithMetadata appendMetadata(
            ParsedLogs parsedLogs,
            String experimentName,
            ObjectId configurationId,
            @Nullable ObjectId computationId
    ) {
        final var now = Instant.now();
        return metadataService.getOrCreateExperiment(new Experiment(now, experimentName, Map.of()))
                .flatMap(experiment -> metadataService.getOrCreateConfiguration(
                        new ExperimentConfiguration(
                                configurationId,
                                experiment.getId(),
                                null,
                                Map.of(),
                                now
                        ))
                        .flatMap(configuration -> metadataService.getOrCreateComputation(
                                new ExperimentComputation(
                                        computationId == null ? ObjectId.get() : computationId,
                                        configuration.getId(),
                                        now
                                ))
                                .map(computation -> {
                                    parsedLogs.getPoints().forEach(p -> tagPointWithComputationId(computation, p));
                                    return new ParsedLogsWithMetadata(
                                            influxPropertiesService.getDefaultBucketName(),
                                            parsedLogs.getPoints(),
                                            configuration,
                                            computation
                                    );
                                })
                        )
                ).block();
    }

    public ParsedLogsWithMetadata appendMetadata(
            AgeParsedLogs parsedLogs,
            String experimentName
    ) {
        var now = Instant.now();
        return metadataService.getOrCreateExperiment(new Experiment(now, experimentName, Map.of()))
                .flatMap(experiment -> metadataService.getOrCreateConfiguration(
                        new ExperimentConfiguration(
                                ObjectId.get(),
                                experiment.getId(),
                                parsedLogs.getConfigurationName(),
                                parsedLogs.getConfigurationParameters(),
                                now
                        ))
                        .flatMap(configuration -> metadataService.getOrCreateComputation(
                                new ExperimentComputation(
                                        ObjectId.get(),
                                        configuration.getId(),
                                        now
                                ))
                                .map(computation -> {
                                    parsedLogs.getPoints().forEach(p -> tagPointWithComputationId(computation, p));
                                    return new ParsedLogsWithMetadata(
                                            influxPropertiesService.getDefaultBucketName(),
                                            parsedLogs.getPoints(),
                                            configuration,
                                            computation
                                    );
                                })
                        )
                ).block();
    }

    private static void tagPointWithComputationId(ExperimentComputation computation, Point point) {
        point.addTag(CommonTags.COMPUTATION_ID, computation.getId().toHexString());
    }

}
