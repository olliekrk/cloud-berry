package com.cloudberry.cloudberry.parsing.service.csv;

import com.cloudberry.cloudberry.common.syntax.ListSyntax;
import com.cloudberry.cloudberry.db.influx.InfluxDefaults;
import com.cloudberry.cloudberry.parsing.model.csv.CsvUploadDetails;
import com.cloudberry.cloudberry.util.FilesUtils;
import com.cloudberry.cloudberry.util.PointTestUtils;
import com.influxdb.client.write.Point;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CsvLogsParserTest {
    private final CsvLogsParser csvLogsParser = new CsvLogsParser();
    private final List<String> csvHeaders = List.of("TIME", "TAG1", "TAG2", "FIELD1", "TAG3", "FIELD2");
    private final List<String> csvFields = ListSyntax.without(csvHeaders, "TIME");

    @Test
    public void parseFileWithTagsShouldResolveAllFieldsAndTags() throws IOException {
        var file = FilesUtils.getFileFromResources("/csv/csv_with_header_row.csv");
        var computationId = new ObjectId();
        var measurementName = "CsvLogsParserTest_Measurement";
        var tagsNames = List.of("TAG1", "TAG2", "TAG3");
        var details = new CsvUploadDetails(tagsNames, new ObjectId(), computationId, measurementName, null);

        var result = csvLogsParser.parseFile(file, details, "");
        var points = result.getPoints();

        assertEquals(2, points.size());

        Point samplePoint = result.getPoints().get(0);
        var tags = PointTestUtils.getTags(samplePoint);
        var fields = PointTestUtils.getFields(samplePoint);
        Number time = PointTestUtils.getTime(samplePoint);

        assertNotNull(tags);
        assertNotNull(fields);
        assertNotNull(time);
        assertEquals(tagsNames.size() + 1, tags.size()); // +1 for computation id tag
        assertTrue(tagsNames.stream().allMatch(tags::containsKey));
        assertTrue(tags.containsKey(InfluxDefaults.CommonTags.COMPUTATION_ID));
        assertTrue(tagsNames.stream().noneMatch(fields::containsKey));
        assertEquals(1234567e6, time.longValue()); // reads from file as millis, stores as nano
    }

    @Test
    public void parseFileWhenNoHeadersInDetailsReadsFirstRowAsHeaders() throws IOException {
        var file = FilesUtils.getFileFromResources("/csv/csv_with_header_row.csv");
        var details = new CsvUploadDetails(List.of(), new ObjectId(), new ObjectId(), "anything", null);
        var result = csvLogsParser.parseFile(file, details, "anything");
        var points = result.getPoints();
        assertEquals(2, points.size());
        var samplePoint = points.get(0);
        assertEquals(Set.copyOf(csvFields), PointTestUtils.getFields(samplePoint).keySet());
    }

    @Test
    public void parseFileWhenHeadersProvidedInDetailsReadsFirstRowAsValues() throws IOException {
        var file = FilesUtils.getFileFromResources("/csv/csv_without_header_row.csv");
        var details = new CsvUploadDetails(List.of(), new ObjectId(), new ObjectId(), "anything", csvHeaders);
        var result = csvLogsParser.parseFile(file, details, "anything");
        var points = result.getPoints();
        assertEquals(2, points.size());
        var samplePoint = points.get(0);
        assertEquals(Set.copyOf(csvFields), PointTestUtils.getFields(samplePoint).keySet());
    }


}
