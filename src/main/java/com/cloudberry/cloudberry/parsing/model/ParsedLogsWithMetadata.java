package com.cloudberry.cloudberry.parsing.model;

import com.cloudberry.cloudberry.db.mongo.data.metadata.ExperimentConfiguration;
import com.cloudberry.cloudberry.db.mongo.data.metadata.ExperimentEvaluation;
import com.influxdb.client.write.Point;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class ParsedLogsWithMetadata {
    private final String bucketName;
    private final List<Point> points;
    private final ExperimentConfiguration experimentConfiguration;
    private final ExperimentEvaluation experimentEvaluation;
}
