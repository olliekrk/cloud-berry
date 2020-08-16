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
    private static final String TEST_FILE = "testLogs/testLog.log";
    private final AgeLogsParser ageLogsParser = new AgeLogsParser();

    @Test
    void properParsedValues() {
        parseTestFile(TEST_FILE)
                .onSuccess(ageParsedLogs -> {
                    assertEquals("labs-config-cuda.xml", ageParsedLogs.getConfigurationName());
                    assertEquals(17, ageParsedLogs.getPoints().size());

                    Map<String, Object> properties = ageParsedLogs.getConfigurationParameters();
                    assertEquals(12, properties.size());

                    assertTrue(properties.containsKey("labs.emas.mutation"));
                    assertEquals(true, properties.get("boolean.value"));
                    assertEquals("andrzej duda", properties.get("string.value"));
                    assertEquals(2137.0, properties.get("double.value"));
                    assertEquals(21.37, properties.get("int.value"));
                });
    }

    @NotNull
    private Try<AgeParsedLogs> parseTestFile(String fileName) {
        var headersKeys = Map.of(
                "[WH]", "[W]",
                "[SH]", "[S]",
                "[BH]", "[B]"
        );
        return parseTestFile(fileName, new AgeUploadDetails(headersKeys, null));
    }

    @NotNull
    private Try<AgeParsedLogs> parseTestFile(String fileName, AgeUploadDetails uploadDetails) {
        return Try.of(() -> ageLogsParser.parseFile(
                FilesUtils.getFileFromResources(fileName),
                uploadDetails
        ));
    }
}
