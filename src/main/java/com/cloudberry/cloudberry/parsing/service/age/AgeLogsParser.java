package com.cloudberry.cloudberry.parsing.service.age;

import com.cloudberry.cloudberry.common.syntax.ArraySyntax;
import com.cloudberry.cloudberry.common.syntax.MapSyntax;
import com.cloudberry.cloudberry.db.influx.InfluxDefaults;
import com.cloudberry.cloudberry.parsing.model.FieldType;
import com.cloudberry.cloudberry.parsing.model.FieldTypes;
import com.cloudberry.cloudberry.parsing.model.age.AgeParsedLogs;
import com.cloudberry.cloudberry.parsing.model.age.AgeUploadDetails;
import com.cloudberry.cloudberry.parsing.service.LogsParser;
import com.cloudberry.cloudberry.util.XmlUtils;
import com.influxdb.client.write.Point;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AgeLogsParser implements LogsParser<AgeUploadDetails> {

    interface AgeXmlProperties {
        String CONFIGURATION_NAME = "computation.title";
    }

    @Override
    public AgeParsedLogs parseFile(File file, AgeUploadDetails uploadDetails, String defaultMeasurementName)
            throws IOException {
        var logHeadersKeys = uploadDetails.getHeadersKeys();
        var logMeasurements = uploadDetails.getHeadersMeasurements();
        var logParametersOrder = new HashMap<String, String[]>();

        var dataPoints = new LinkedList<Point>();
        var configurationName = uploadDetails.getConfigurationName();
        Map<String, Object> configurationParameters = null;

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
                            logMeasurement,
                            uploadDetails.getFieldTypes()
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
                    var xmlConfigurationName = configurationParameters.get(AgeXmlProperties.CONFIGURATION_NAME);
                    if (configurationName == null && xmlConfigurationName != null) {
                        configurationName = String.valueOf(xmlConfigurationName);
                    }
                } else {
                    log.warn("Header {} not parsed", lineKey);
                }
            }
        }

        return new AgeParsedLogs(dataPoints, configurationName, configurationParameters);
    }

    private Map<String, Object> getXmlMap(List<String> xmlLogs) {
        return xmlLogs.stream()
                .map(xmlLog -> xmlLog.split("=", 2))
                .collect(
                        Collectors.toMap(
                                log -> log[0].trim(),
                                log -> parseField(log[1].trim(), FieldType.NUMBER)
                        )
                );
    }

    private Point getMeasurementPoint(
            String[] parametersNames,
            String[] parametersValuesRaw,
            String measurementName,
            FieldTypes fieldTypes
    ) {
        var parametersNamesList = ArraySyntax.linkedList(parametersNames);
        var parametersValues = ArraySyntax.linkedList(parametersValuesRaw);

        // extract time field
        var timeIndex = Arrays.asList(parametersNames).indexOf(TIME_COLUMN_NAME);
        Instant time = null;
        if (timeIndex >= 0) {
            time = parseTime(parametersValues.get(timeIndex));
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
                        (field, rawValue) -> parseField(rawValue, fieldTypes.getOrDefault(field))
                ));
    }
}
