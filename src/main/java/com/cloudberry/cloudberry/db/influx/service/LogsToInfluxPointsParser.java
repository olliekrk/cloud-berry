package com.cloudberry.cloudberry.db.influx.service;

import com.cloudberry.cloudberry.service.LogsParser;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.Long.parseLong;

@Slf4j
@Service
@RequiredArgsConstructor
@Profile("influx")
public class LogsToInfluxPointsParser implements LogsParser<Point> {
    private static final String MEASUREMENT_PREFIX = "xdxdxd_";

    private final Map<String, String> keys = Map.of("[WH]", "[W]", "[SH]", "[S]", "[BH]", "[B]");
    private Map<String, String[]> parameters = new HashMap<>();

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

        for (String rawLog : logs) {
            String[] log = rawLog.trim().split(";");

            String logPrefix = log[0];
            if (keys.containsKey(logPrefix)) {
                saveParametersOrder(logPrefix, log);
            } else if (keys.containsValue(logPrefix)) {
                points.add(getMeasurementPoint(logPrefix, log));
            } else {
                LogsToInfluxPointsParser.log.warn("Header {} not parsed", log[0]); //todo handle e.g. ExperimentConfiguration
            }
        }
        return points;
    }

    private Point getMeasurementPoint(String logType, String[] log) {
        Point point = Point.measurement(MEASUREMENT_PREFIX + logType)
                .addFields(getMapFromArrays(parameters.get(logType), log));

        if (parameters.get(logType)[1].equalsIgnoreCase("time")) {
            long time = parseLong(log[1]);
            point.time(time, WritePrecision.MS);
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
                .collect(Collectors.toMap(i -> keys[i], i -> findType(values[i])));
    }

    private Object findType(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return value;
        }
    }
}
