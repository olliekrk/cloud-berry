package com.cloudberry.cloudberry.rest.api.upload.files;

import com.cloudberry.cloudberry.db.mongo.data.logs.MongoBestSolutionLog;
import com.cloudberry.cloudberry.db.mongo.data.logs.MongoLog;
import com.cloudberry.cloudberry.db.mongo.data.logs.MongoSummaryLog;
import com.cloudberry.cloudberry.db.mongo.data.logs.MongoWorkplaceLog;
import com.cloudberry.cloudberry.model.solution.Solution;
import com.cloudberry.cloudberry.model.solution.SolutionDetails;
import com.cloudberry.cloudberry.repository.facades.LogsRepositoryFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.lang.Double.parseDouble;
import static java.lang.Long.parseLong;

@Slf4j
@RequiredArgsConstructor
@Service
public class MongoLogsParser implements LogsParser {

    private final LogsRepositoryFacade repositoryFacade;

    private final Map<Integer, String> workplaceParametersPosition = new HashMap<>();
    private boolean workplaceParametersSaved;
    private UUID evaluationId;

    public boolean saveLogsToDatabase(String rawLogs) {
        this.evaluationId = UUID.randomUUID();
        List<MongoLog> logs = parseLogs(rawLogs);
        logs.stream().map(repositoryFacade::save).forEach(Mono::block);
        return true;
    }

    private List<MongoLog> parseLogs(String rawLogs) {
        this.workplaceParametersSaved = false;
        String[] logs = rawLogs.split("\n");
        List<MongoLog> mongoLogs = new LinkedList<>();
        for (String rawLog : logs) {
            String[] log = rawLog.trim().split(";");

            switch (log[0]) {
                case "[WH]" -> saveWorkplaceParametersOrder(log);
                case "[W]" -> mongoLogs.add(getMongoWorkplaceLog(log));
                case "[S]" -> mongoLogs.add(getMongoSummaryLog(log));
                case "[B]" -> mongoLogs.add(getMongoBestSolutionLog(log));
                default -> MongoLogsParser.log.warn("Header {} not parsed", log[0]); //todo handle e.g. ExperimentConfiguration
            }
        }
        return mongoLogs;
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

    /**
     * header format: [WH];TIME;WORKPLACE_ID;POPULATION_SIZE;AVERAGE_FITNESS;STEP_NUMBER;ENERGY_SUM
     *
     * @param log example: [W];5008460671;0;104;4.596490894420629;4662;2474.5474458052977
     */
    private MongoLog getMongoWorkplaceLog(String[] log) {
        final Instant time = Instant.ofEpochMilli(parseLong(log[1]));
        final long workplaceId = parseLong(log[2]);
        final Map<String, Object> parameters = new HashMap<>();
        for (int i = 3; i < log.length; i++) {
            parameters.put(workplaceParametersPosition.get(i), log[i]);
        }
        return new MongoWorkplaceLog(time, evaluationId, workplaceId, parameters);
    }

    /**
     * header format: [SH];TIME;BEST_SOLUTION_SO_FAR;FITNESS_EVALUATIONS
     *
     * @param log example: [S];5010931007;6.174939467312349;1180875
     */
    private MongoLog getMongoSummaryLog(String[] log) {
        final Instant time = Instant.ofEpochMilli(parseLong(log[1]));
        final double bestEvaluation = parseDouble(log[2]);
        final long evaluationsCount = parseLong(log[3]);
        return new MongoSummaryLog(time, evaluationId, bestEvaluation, evaluationsCount);
    }

    /**
     * header format: [BH];SOLUTION_STRING;SOLUTION_WORKPLACE_ID;SOLUTION_WORKPLACE_STEP_NUMBER;SOLUTION_OCURRANCE_COUNT
     *
     * @param log example: [B];[ - 30 2 4 2 2 1 1 3 1 2 2 1 3 2 2 2 2 1 1 1 1 4 1 1 4 1 1 2 1 2 1 2 1 1 1 2 1 9 ];0;2062909;235
     */
    private MongoLog getMongoBestSolutionLog(String[] log) {
        final Solution solution = new Solution(Map.of("SOLUTION_STRING", log[1]));
        final long workplaceId = parseLong(log[2]);
        final long stepNumber = parseLong(log[3]);
        final long occurrencesCount = parseLong(log[4]);
        final SolutionDetails solutionDetails = new SolutionDetails(workplaceId, stepNumber, occurrencesCount);
        return new MongoBestSolutionLog(Instant.now(), evaluationId, solution, solutionDetails);
    }
}
