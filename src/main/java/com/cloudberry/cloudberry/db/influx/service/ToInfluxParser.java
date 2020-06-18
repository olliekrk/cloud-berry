package com.cloudberry.cloudberry.db.influx.service;

import com.cloudberry.cloudberry.db.mongo.data.logs.MongoLog;
import com.cloudberry.cloudberry.db.mongo.data.logs.MongoWorkplaceLog;
import com.cloudberry.cloudberry.kafka.event.EventType;
import com.cloudberry.cloudberry.service.LogsParser;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

import static java.lang.Long.parseLong;

@Slf4j
@Service
@RequiredArgsConstructor
@Profile("influx")
public class ToInfluxParser implements LogsParser {
    private final InfluxMeasurementWriter influxDBConnector;

    private final Map<Integer, String> workplaceParametersPosition = new HashMap<>();
    private boolean workplaceParametersSaved;
    private UUID evaluationId;

    public boolean saveLogsToDatabase(String rawLogs) {
        this.evaluationId = UUID.randomUUID();
        parseLogs(rawLogs);
        return true;
    }

    private void parseLogs(String rawLogs) {
        this.workplaceParametersSaved = false;
        String[] logs = rawLogs.split("\n");
        Map<EventType, List<MongoLog>> mapReturned = new HashMap<>();
        mapReturned.put(EventType.BEST_SOLUTION, new LinkedList<>());
        mapReturned.put(EventType.SUMMARY, new LinkedList<>());
        mapReturned.put(EventType.WORKPLACE, new LinkedList<>());

        for (String rawLog : logs) {
            String[] log = rawLog.trim().split(";");

            switch (log[0]) {
                case "[WH]" -> saveWorkplaceParametersOrder(log);
                case "[W]" -> {
                    writeWorkplaceLog(log);
                    //todo
                }
                case "[S]" -> {
                    //todo
                }
                case "[B]" -> {
                    //todo
                }
                default -> ToInfluxParser.log.warn("Header {} not parsed", log[0]); //todo handle e.g. ExperimentConfiguration
            }
        }
    }

    /**
     * @param log example: [WH];TIME;WORKPLACE_ID;POPULATION_SIZE;AVERAGE_FITNESS;STEP_NUMBER;ENERGY_SUM
     */
    private void saveWorkplaceParametersOrder(String[] log) {
        if (workplaceParametersSaved) {
            throw new IllegalArgumentException("Log with prefix [\"WH\"] already specified.");
        }
        for (int i = 0; i < log.length; i++) {
            workplaceParametersPosition.put(i, log[i]);
        }
        workplaceParametersSaved = true;
    }

    private void writeWorkplaceLog(String[] log) {
        final Instant time = Instant.ofEpochMilli(parseLong(log[1]));
        final long workplaceId = parseLong(log[2]);
        final Map<String, Object> parameters = new HashMap<>();
        for (int i = 3; i < log.length; i++) {
            parameters.put(workplaceParametersPosition.get(i), log[i]);
        }
//        influxDBConnector.writePoint(Point.measurement("h2o_feet")
//                .time(System.currentTimeMillis(), WritePrecision.MS)
//                .addTag("location", "coyote_creek")
//                .addField("level description", "between 6 and 9 feet")
//                .addField("water_level", 8.12d));
    }
}
