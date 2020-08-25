package com.cloudberry.cloudberry.parsing.service;

import com.cloudberry.cloudberry.config.influx.InfluxConfig;
import com.cloudberry.cloudberry.db.influx.InfluxDefaults.CommonTags;
import com.cloudberry.cloudberry.db.mongo.data.metadata.Experiment;
import com.cloudberry.cloudberry.db.mongo.data.metadata.ExperimentComputation;
import com.cloudberry.cloudberry.db.mongo.data.metadata.ExperimentConfiguration;
import com.cloudberry.cloudberry.db.mongo.service.MetadataService;
import com.cloudberry.cloudberry.parsing.model.ParsedLogs;
import com.cloudberry.cloudberry.parsing.model.ParsedLogsWithMetadata;
import com.cloudberry.cloudberry.parsing.model.age.AgeParsedLogs;
import io.vavr.Tuple;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogsMetadataAppender {
    private final InfluxConfig influxConfig;
    private final MetadataService metadataService;

    public ParsedLogsWithMetadata appendMetadata(ParsedLogs parsedLogs,
                                                 String experimentName,
                                                 ObjectId configurationId,
                                                 ObjectId computationId) {
        var now = Instant.now();
        return metadataService
                .getOrCreateExperiment(new Experiment(new ObjectId(), experimentName, Map.of(), now))
                .map(Tuple::of)
                .flatMap(meta -> {
                    var configuration = new ExperimentConfiguration(configurationId, meta._1.getId(), null, Map.of(), now);
                    return metadataService.getOrCreateConfiguration(configuration).map(meta::append);
                })
                .flatMap(meta -> {
                    var computation = new ExperimentComputation(computationId, meta._2.getId(), now);
                    return metadataService.getOrCreateComputation(computation).map(meta::append);
                })
                .map(tuple -> new ParsedLogsWithMetadata(influxConfig.getDefaultBucketName(), parsedLogs.getPoints(), tuple._2, tuple._3))
                .block();
    }

    public ParsedLogsWithMetadata appendMetadata(AgeParsedLogs parsedLogs, String experimentName) {
        var now = Instant.now();
        return metadataService
                .getOrCreateExperiment(new Experiment(new ObjectId(), experimentName, Map.of(), now))
                .map(Tuple::of)
                .flatMap(meta -> {
                    var configuration = new ExperimentConfiguration(
                            new ObjectId(),
                            meta._1.getId(),
                            parsedLogs.getConfigurationName(),
                            parsedLogs.getConfigurationParameters(),
                            now
                    );
                    return metadataService.getOrCreateConfiguration(configuration).map(meta::append);
                })
                .flatMap(meta -> {
                    var computation = new ExperimentComputation(new ObjectId(), meta._2.getId(), now);
                    return metadataService.getOrCreateComputation(computation).map(meta::append);
                })
                .doOnNext(meta -> {
                    var computationId = meta._3.getId().toHexString();
                    parsedLogs.getPoints().forEach(point -> point.addTag(CommonTags.COMPUTATION_ID, computationId));
                })
                .map(tuple -> new ParsedLogsWithMetadata(influxConfig.getDefaultBucketName(), parsedLogs.getPoints(), tuple._2, tuple._3))
                .block();
    }

}
