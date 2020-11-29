package com.cloudberry.cloudberry.parsing.service.age;

import com.cloudberry.cloudberry.parsing.model.FieldTypes;
import com.cloudberry.cloudberry.parsing.model.age.AgeParsedLogs;
import com.cloudberry.cloudberry.parsing.model.age.AgeUploadDetails;
import com.cloudberry.cloudberry.util.FilesUtils;
import io.vavr.control.Try;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AgeLogsParserTest {
    private static final String TEST_FILE = "/testLogs/age_log_1.log";
    private static final String TEST_FILE_CONFIGURATION_NAME = "LABS=101 on GPU, TabuSearch-128";
    private static final Map<String, String> TEST_FILE_KEYS = Map.of(
            "[WH]", "[W]",
            "[SH]", "[S]",
            "[BH]", "[B]"
    );

    private final AgeLogsParser ageLogsParser = new AgeLogsParser();

    @Test
    void parseFileReadsXmlProperties() {
        var ageParsedLogs = getAgeParsedLogs(null);

        assertEquals(TEST_FILE_CONFIGURATION_NAME, ageParsedLogs.getConfigurationName());
        assertEquals(17, ageParsedLogs.getPoints().size());

        Map<String, Object> properties = ageParsedLogs.getConfigurationParameters();
        assertEquals(12, properties.size());

        assertTrue(properties.containsKey("labs.problem.cuda.awaitLimitInMicroS"));
        assertEquals("true", properties.get("boolean.value"));
        assertEquals("andrzej duda", properties.get("string.value"));
        assertEquals(21.37, properties.get("double.value"));
        assertEquals(2137.0, properties.get("int.value"));
    }

    @Test
    void parseFileNoExplicitConfigurationNameReturnsXmlConfigurationName() {
        var ageParsedLogs = getAgeParsedLogs(null);

        assertEquals(TEST_FILE_CONFIGURATION_NAME, ageParsedLogs.getConfigurationName());
    }

    @Test
    void parseFileExplicitConfigurationNameReturnsExplicitConfigurationName() {
        var explicitConfigurationName = "MyExperimentConfiguration1";

        var ageParsedLogs = getAgeParsedLogs(explicitConfigurationName);

        assertEquals(explicitConfigurationName, ageParsedLogs.getConfigurationName());
    }

    private AgeParsedLogs getAgeParsedLogs(String configurationName) {
        return Try.of(() ->
                              ageLogsParser.parseFile(
                                      FilesUtils.getFileFromResources(TEST_FILE),
                                      new AgeUploadDetails(TEST_FILE_KEYS, null, configurationName, FieldTypes.empty()),
                                      ""
                              )
        ).get();
    }
}
