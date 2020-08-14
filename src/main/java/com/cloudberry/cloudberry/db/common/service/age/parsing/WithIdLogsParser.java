package com.cloudberry.cloudberry.db.common.service.age.parsing;

import com.cloudberry.cloudberry.db.common.data.ParsedLogs;
import com.cloudberry.cloudberry.db.common.data.SimpleParsedExperiment;
import com.cloudberry.cloudberry.db.influx.InfluxDefaults;
import com.cloudberry.cloudberry.db.mongo.data.metadata.Experiment;
import com.cloudberry.cloudberry.db.mongo.data.metadata.ExperimentConfiguration;
import com.cloudberry.cloudberry.db.mongo.data.metadata.ExperimentEvaluation;
import com.cloudberry.cloudberry.db.mongo.service.MetadataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class WithIdLogsParser {
    @Value("${influx.buckets.default-logs}")
    private String defaultLogsBucketName;
    private final MetadataService metadataService;

    public ParsedLogs parseLogsWithId(SimpleParsedExperiment simpleParsedExperiment, String experimentName) {
        var now = Instant.now();

        var experiment = metadataService
                .getOrCreateExperiment(new Experiment(
                        new ObjectId(), experimentName, Map.of(), now))
                .block();

        var configuration = metadataService
                .getOrCreateConfiguration(new ExperimentConfiguration(
                        new ObjectId(),
                        experiment.getId(),
                        simpleParsedExperiment.getConfigurationName(),
                        simpleParsedExperiment.getConfigurationParameters(),
                        now)
                ).block();

        var evaluation = metadataService
                .createEvaluation(new ExperimentEvaluation(new ObjectId(), configuration.getId(), now))
                .block();

        var evaluationId = evaluation.getId().toHexString();

        simpleParsedExperiment.getPoints().forEach(point ->
                point.addTag(InfluxDefaults.CommonTags.EVALUATION_ID, evaluationId)
        );

        return new ParsedLogs(defaultLogsBucketName, simpleParsedExperiment.getPoints(), configuration, evaluation);
    }
}
