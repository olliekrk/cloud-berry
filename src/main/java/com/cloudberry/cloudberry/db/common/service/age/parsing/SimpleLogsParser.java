package com.cloudberry.cloudberry.db.common.service.age.parsing;

import com.cloudberry.cloudberry.db.common.data.ImportDetails;
import com.cloudberry.cloudberry.db.common.data.SimpleParsedExperiment;
import com.cloudberry.cloudberry.db.influx.InfluxDefaults;
import com.cloudberry.cloudberry.util.XmlUtils;
import com.cloudberry.cloudberry.util.syntax.ArraySyntax;
import com.cloudberry.cloudberry.util.syntax.MapSyntax;
import com.influxdb.client.write.Point;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Streams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.lang.Long.parseLong;

@Slf4j
@Service
@RequiredArgsConstructor
public class SimpleLogsParser {
    @Value("${influx.measurements.default-measurement-name}")
    private String defaultMeasurementName;
    private static final String LOG_TIME_COLUMN = "TIME";

    public SimpleParsedExperiment parseFile(File file, ImportDetails importDetails) throws IOException {
        var logHeadersKeys = importDetails.getHeadersKeys();
        var logMeasurements = importDetails.getHeadersMeasurements();
        var logParametersOrder = new HashMap<String, String[]>();
        String configurationName = null;
        Map<String, Object> configurationParameters = null;
        List<Point> dataPoints = new LinkedList<>();

        Function<String[], String[]> skipLineKey = line -> Arrays.stream(line).skip(1).toArray(String[]::new);

        try (var reader = new BufferedReader(new FileReader(file))) {
            var lines = reader.lines().toArray(String[]::new); // todo: try making it lazy
            for (int i = 0; i < lines.length; i++) {
                String line = lines[i];
                String[] lineParts = line.trim().split(";");

                String lineKey = lineParts[0];
                if (logHeadersKeys.containsKey(lineKey)) { // line is a header row
                    logParametersOrder.put(logHeadersKeys.get(lineKey), skipLineKey.apply(lineParts));
                } else if (logHeadersKeys.containsValue(lineKey)) { // line is a value row
                    var logMeasurement = logMeasurements.getOrDefault(lineKey, defaultMeasurementName);
                    var logParameters = skipLineKey.apply(lineParts);
                    var point = getMeasurementPoint(
                            logParametersOrder.get(lineKey),
                            logParameters,
                            logMeasurement
                    );
                    dataPoints.add(point);
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
                    log.warn("Header {} not parsed", lineKey);
                }
            }
        }

        return new SimpleParsedExperiment(configurationName, configurationParameters, dataPoints);
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

    private Point getMeasurementPoint(String[] parametersNames,
                                      String[] parametersValuesRaw,
                                      String measurementName) {
        var parametersNamesList = ArraySyntax.linkedList(parametersNames);
        var parametersValues = ArraySyntax.linkedList(parametersValuesRaw);

        // extract time field
        var timeIndex = Arrays.asList(parametersNames).indexOf(LOG_TIME_COLUMN);
        Instant time = null;
        if (timeIndex >= 0) {
            time = Instant.ofEpochMilli(Long.parseLong(parametersValues.get(timeIndex)));
            parametersNamesList.remove(timeIndex);
            parametersValues.remove(timeIndex);
        }

        var parametersNamesFiltered = parametersNamesList.toArray(String[]::new);
        var parametersValuesFiltered = parametersValues.toArray(String[]::new);

        return Point
                .measurement(measurementName)
                .time(time != null ? time : Instant.EPOCH, InfluxDefaults.WRITE_PRECISION)
                .addFields(MapSyntax.zippedArrays(
                        parametersNamesFiltered,
                        parametersValuesFiltered,
                        SimpleLogsParser::parseValue
                ));
    }

    private Map<String, Object> getMapFromArrays(String[] keys, String[] values) {
        return IntStream.range(0, keys.length).boxed()
                .collect(Collectors.toMap(i -> keys[i], i -> parseValue(values[i])));
    }

    private static Object parseValue(String value) {
        return Try.of(() -> (Object) Double.parseDouble(value))
                .orElse(Try.of(() -> (Object) Boolean.parseBoolean(value)))
                .getOrElse(value);
    }
}
