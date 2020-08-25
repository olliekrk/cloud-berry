package com.cloudberry.cloudberry.parsing.service.csv;

import com.cloudberry.cloudberry.db.influx.InfluxDefaults;
import com.cloudberry.cloudberry.parsing.model.csv.CsvUploadDetails;
import com.cloudberry.cloudberry.util.FilesUtils;
import com.influxdb.client.write.Point;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class CsvLogsParserTest {
    private final CsvLogsParser csvLogsParser = new CsvLogsParser();

    @Test
    public void parseFileWithTagsShouldResolveAllFieldsAndTags() throws IOException {
        var file = FilesUtils.getFileFromResources("/testLogs/csv_1row.csv");
        var computationId = new ObjectId();
        var measurementName = "CsvLogsParserTest_Measurement";
        var tagsNames = List.of("TAG1", "TAG2", "TAG3");
        var details = new CsvUploadDetails(tagsNames, new ObjectId(), computationId, measurementName);

        var result = csvLogsParser.parseFile(file, details, "");
        var points = result.getPoints();

        assertEquals(1, points.size());

        // 16.08.20:  influx point can be only accessed with reflection api
        Point samplePoint = result.getPoints().get(0);
        Map<String, String> tags = (Map<String, String>) ReflectionTestUtils.getField(samplePoint, "tags");
        Map<String, Object> fields = (Map<String, Object>) ReflectionTestUtils.getField(samplePoint, "fields");
        Number time = (Number) ReflectionTestUtils.getField(samplePoint, "time");

        assertNotNull(tags);
        assertNotNull(fields);
        assertNotNull(time);
        assertEquals(tagsNames.size() + 1, tags.size()); // +1 for computation id tag
        assertTrue(tagsNames.stream().allMatch(tags::containsKey));
        assertTrue(tags.containsKey(InfluxDefaults.CommonTags.COMPUTATION_ID));
        assertTrue(tagsNames.stream().noneMatch(fields::containsKey));
        assertEquals(1234567e6, time.longValue()); // reads from file as millis, stores as nano
    }

}
