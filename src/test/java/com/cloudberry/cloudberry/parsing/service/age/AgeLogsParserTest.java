package com.cloudberry.cloudberry.parsing.service.age;

import com.cloudberry.cloudberry.parsing.model.age.AgeParsedLogs;
import com.cloudberry.cloudberry.parsing.model.age.AgeUploadDetails;
import com.cloudberry.cloudberry.util.FilesUtils;
import io.vavr.control.Try;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AgeLogsParserTest {
    private static final String TEST_FILE = "/testLogs/testLog.log";
    private final AgeLogsParser ageLogsParser = new AgeLogsParser();

    @Test
    void properParsedValues() {
        var ageParsedLogs = parseTestFile(TEST_FILE);

        assertEquals("labs-config-cuda.xml", ageParsedLogs.getConfigurationName());
        assertEquals(17, ageParsedLogs.getPoints().size());

        Map<String, Object> properties = ageParsedLogs.getConfigurationParameters();
        assertEquals(12, properties.size());

        assertTrue(properties.containsKey("labs.problem.cuda.awaitLimitInMicroS"));
        assertEquals("true", properties.get("boolean.value"));
        assertEquals("andrzej duda", properties.get("string.value"));
        assertEquals(21.37, properties.get("double.value"));
        assertEquals(2137.0, properties.get("int.value"));
    }

    @NotNull
    private AgeParsedLogs parseTestFile(String fileName) {
        var headersKeys = Map.of(
                "[WH]", "[W]",
                "[SH]", "[S]",
                "[BH]", "[B]"
        );
        var headerMeasurements = Map.of(
                "[W]", "workplace_log",
                "[S]", "summary_log",
                "[B]", "best_solution_log"
        );
        return parseTestFile(fileName, new AgeUploadDetails(headersKeys, headerMeasurements));
    }

    @NotNull
    private AgeParsedLogs parseTestFile(String fileName, AgeUploadDetails uploadDetails) {
        return Try.of(() -> ageLogsParser.parseFile(
                FilesUtils.getFileFromResources(fileName),
                uploadDetails,
                ""
        )).get();
    }
}
