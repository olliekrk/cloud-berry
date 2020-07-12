package com.cloudberry.cloudberry.db.common.service;

import com.cloudberry.cloudberry.db.common.data.ImportDetails;
import com.cloudberry.cloudberry.db.common.data.ParsedLogs;
import com.cloudberry.cloudberry.db.influx.InfluxDefaults;
import com.cloudberry.cloudberry.db.mongo.data.metadata.Experiment;
import com.cloudberry.cloudberry.db.mongo.data.metadata.ExperimentConfiguration;
import com.cloudberry.cloudberry.db.mongo.data.metadata.ExperimentEvaluation;
import com.cloudberry.cloudberry.db.mongo.service.MetadataService;
import com.cloudberry.cloudberry.util.XmlUtils;
import com.influxdb.client.write.Point;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.Long.parseLong;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogsParserService implements LogsParser<ParsedLogs> {
    @Value("${influx.buckets.default-logs}")
    private String defaultLogsBucketName;
    private final MetadataService metadataService;

    @Override
    public ParsedLogs parseExperimentFile(File file, String experimentName, ImportDetails importDetails) throws IOException {
        var now = Instant.now();
        var headerKeys = importDetails.getHeadersKeys();
        var headerMeasurements = importDetails.getHeadersMeasurements();
        var headerParameters = new HashMap<String, String[]>();
        String configurationName = null;
        Map<String, Object> configurationParameters = null;

        List<Point> dataPoints = new LinkedList<>();
        try (var reader = new BufferedReader(new FileReader(file))) {
            var lines = reader.lines().toArray(String[]::new); // todo: try making it lazy
            for (int i = 0; i < lines.length; i++) {
                String line = lines[i];
                String[] lineParts = line.trim().split(";");

                String logPrefix = lineParts[0];
                if (headerKeys.containsKey(logPrefix)) {
                    saveParametersOrder(headerParameters, headerKeys, logPrefix, lineParts);
                } else if (headerKeys.containsValue(logPrefix)) {
                    dataPoints.add(getMeasurementPoint(headerParameters, headerMeasurements, logPrefix, lineParts));
                } else if (line.startsWith("<")) {
                    List<String> xmlLogs = new LinkedList<>();
                    String tagName = XmlUtils.getTagName(line);
                    String closingTag = null;
                    while (!tagName.equals(closingTag)) {
                        i++;
                        line = lines[i];
                        xmlLogs.add(line);
                        closingTag = XmlUtils.getClosingTagName(line);
                    }
                    xmlLogs.remove(line); //remove closing tag from list
                    configurationParameters = getXmlMap(xmlLogs);
                    configurationName = String.valueOf(configurationParameters.get("file"));
                } else {
                    log.warn("Header {} not parsed", lineParts[0]);
                }
            }
        }

        var experiment = metadataService
                .getOrCreateExperiment(new Experiment(new ObjectId(), experimentName, Map.of(), now))
                .block();

        var configuration = metadataService
                .getOrCreateConfiguration(new ExperimentConfiguration(new ObjectId(), experiment.getId(), configurationName, configurationParameters, now))
                .block();

        var evaluation = metadataService
                .createEvaluation(new ExperimentEvaluation(new ObjectId(), configuration.getId(), now))
                .block();

        dataPoints.forEach(point ->
                point.addTag(InfluxDefaults.CommonTags.EVALUATION_ID, evaluation.getId().toHexString())
        );

        return new ParsedLogs(defaultLogsBucketName, dataPoints, configuration, evaluation);
    }

    private Map<String, Object> getXmlMap(List<String> xmlLogs) {
        return xmlLogs.stream()
                .map(xmlLog -> xmlLog.split("=", 2))
                .collect(
                        Collectors.toMap(
                                log -> log[0].trim(),
                                log -> parseValue(log[1].trim())
                        )
                );
    }

    private Point getMeasurementPoint(Map<String, String[]> parameters,
                                      Map<String, String> headerMeasurements,
                                      String logType,
                                      String[] log) {
        Point point = Point
                .measurement(headerMeasurements.getOrDefault(logType, "unknown_log"))
                .addFields(getMapFromArrays(parameters.get(logType), log));

        if (parameters.get(logType)[1].equalsIgnoreCase("time")) {
            long time = parseLong(log[1]);
            point.time(time, InfluxDefaults.WRITE_PRECISION);
        }

        return point;
    }

    /**
     * @param log examples:
     *            [WH];TIME;WORKPLACE_ID;POPULATION_SIZE;AVERAGE_FITNESS;STEP_NUMBER;ENERGY_SUM
     */
    private void saveParametersOrder(Map<String, String[]> parameters,
                                     Map<String, String> headerKeys,
                                     String logType,
                                     String[] log) {
        String logKey = headerKeys.get(logType);
        parameters.compute(logKey, (s, fieldNames) -> {
            if (fieldNames == null) {
                return log;
            }
            throw new IllegalArgumentException(String.format("Log with prefix [%s] already specified.", logType));
        });
    }

    private Map<String, Object> getMapFromArrays(String[] keys, String[] values) {
        return IntStream.range(0, keys.length).boxed()
                .collect(Collectors.toMap(i -> keys[i], i -> parseValue(values[i])));
    }

    private Object parseValue(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return value;
        }
    }

}
