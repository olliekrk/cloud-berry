package com.cloudberry.cloudberry.db.influx.service;

import com.cloudberry.cloudberry.db.influx.InfluxDefaults;
import com.cloudberry.cloudberry.service.LogsParser;
import com.influxdb.client.write.Point;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.Long.parseLong;

@Slf4j
@Service
@RequiredArgsConstructor
@Profile("influx")
public class InfluxLogsParser implements LogsParser<Point> {
    private static final String MEASUREMENT_PREFIX = "measurement_";

    private final Map<String, String> keys = Map.of("[WH]", "[W]", "[SH]", "[S]", "[BH]", "[B]");
    private Map<String, String[]> parameters = new HashMap<>();
    Point configurationPoint;

    @Override
    public List<Point> parseMeasurements(String rawLogs) {
        parameters = new HashMap<>();
        String evaluationId = UUID.randomUUID().toString();
        return parseLogs(rawLogs).stream()
                .map(point -> point.addTag("evaluation_id", evaluationId))
                .collect(Collectors.toList());
    }

    private List<Point> parseLogs(String rawLogs) {
        String[] logs = rawLogs.split("\n");
        List<Point> points = new LinkedList<>();

        for (int i = 0; i < logs.length; i++) {
            String rawLog = logs[i];
            String[] log = rawLog.trim().split(";");

            String logPrefix = log[0];
            if (keys.containsKey(logPrefix)) {
                saveParametersOrder(logPrefix, log);
            } else if (keys.containsValue(logPrefix)) {
                points.add(getMeasurementPoint(logPrefix, log));
            } else if (rawLog.contains("<")) {
                List<String> xmlLogs = new LinkedList<>();
                String tagName = XmlUtils.getTagName(rawLog);
                String closingTag = null;
                while (!tagName.equals(closingTag)) {
                    i++;
                    rawLog = logs[i];
                    xmlLogs.add(rawLog);
                    closingTag = XmlUtils.getClosingTagName(rawLog);
                }
                xmlLogs.remove(rawLog); //remove closing tag from list
                var map = getXmlMap(xmlLogs);
                this.configurationPoint = Point.measurement(tagName)
                        .addFields(map); //todo save it
            } else {
                InfluxLogsParser.log.warn("Header {} not parsed", log[0]);
            }
        }
        return points;
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

    private Point getMeasurementPoint(String logType, String[] log) {
        Point point = Point.measurement(MEASUREMENT_PREFIX + logType)
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
     *            [SH];TIME;BEST_SOLUTION_SO_FAR;FITNESS_EVALUATIONS
     */
    private void saveParametersOrder(String logType, String[] log) {
        String logKey = keys.get(logType);
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
