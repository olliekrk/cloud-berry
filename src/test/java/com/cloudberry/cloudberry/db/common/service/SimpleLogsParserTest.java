package com.cloudberry.cloudberry.db.common.service;

import com.cloudberry.cloudberry.db.common.data.ImportDetails;
import com.cloudberry.cloudberry.db.common.data.SimpleParsedExperiment;
import com.cloudberry.cloudberry.db.common.service.age.parsing.SimpleLogsParser;
import com.cloudberry.cloudberry.util.FilesUtils;
import io.vavr.control.Try;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SimpleLogsParserTest {
    private static final String TEST_FILE = "testLogs/testLog.log";
    private final SimpleLogsParser simpleLogsParser = new SimpleLogsParser();

    @Test
    void properParsedValues() {
        parseTestFile(TEST_FILE)
                .onSuccess(simpleParsedExperiment -> {
                    assertEquals("labs-config-cuda.xml", simpleParsedExperiment.getConfigurationName());
                    assertEquals(17, simpleParsedExperiment.getPoints().size());

                    Map<String, Object> properties = simpleParsedExperiment.getConfigurationParameters();
                    assertEquals(12, properties.size());

                    assertTrue(properties.containsKey("labs.emas.mutation"));
                    assertEquals(true, properties.get("boolean.value"));
                    assertEquals("andrzej duda", properties.get("string.value"));
                    assertEquals(2137.0, properties.get("double.value"));
                    assertEquals(21.37, properties.get("int.value"));
                });
    }

    @NotNull
    private Try<SimpleParsedExperiment> parseTestFile(String fileName) {
        return parseTestFile(fileName, new ImportDetails(null, null));
    }

    @NotNull
    private Try<SimpleParsedExperiment> parseTestFile(String fileName, ImportDetails importDetails) {
        return Try.of(() -> simpleLogsParser.parseFile(
                FilesUtils.getFileFromResources(fileName),
                importDetails
        ));
    }
}
