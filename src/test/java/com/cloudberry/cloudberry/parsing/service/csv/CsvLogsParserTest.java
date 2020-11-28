package com.cloudberry.cloudberry.parsing.service.csv;

import com.cloudberry.cloudberry.parsing.model.FieldTypes;
import com.cloudberry.cloudberry.parsing.model.csv.CsvUploadDetails;
import com.cloudberry.cloudberry.util.FilesUtils;
import com.cloudberry.cloudberry.util.PointTestUtils;
import com.influxdb.client.write.Point;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CsvLogsParserTest {
    private final CsvLogsParser csvLogsParser = new CsvLogsParser();
    private final List<String> csvFields = List.of("FIELD1", "FIELD2");
    private final List<String> csvTags = List.of("TAG1", "TAG2", "TAG3");
    private final List<String> csvHeaders = new LinkedList<>();
    private final int numberOfValueRows = 2;

    {
        csvHeaders.add(CsvLogsParser.TIME_COLUMN_NAME);
        csvHeaders.addAll(csvTags);
        csvHeaders.addAll(csvFields);
    }

    @Test
    public void parseFileWithTagsShouldResolveAllFieldsAndTags() throws IOException {
        var file = FilesUtils.getFileFromResources("/csv/csv_with_header_row.csv");
        var computationId = new ObjectId();
        var measurementName = "CsvLogsParserTest_Measurement";
        var details = new CsvUploadDetails(csvTags, new ObjectId(), computationId, measurementName, null,
                                           FieldTypes.empty()
        );

        var result = csvLogsParser.parseFile(file, details, "");
        var points = result.getPoints();

        assertEquals(numberOfValueRows, points.size());

        Point samplePoint = result.getPoints().get(0);
        Number time = PointTestUtils.getTime(samplePoint);
        var tags = PointTestUtils.getTags(samplePoint);
        var fields = PointTestUtils.getFields(samplePoint);

        assertNotNull(tags);
        assertNotNull(fields);
        assertNotNull(time);
        assertEquals(csvTags.size(), tags.size());
        assertTrue(csvTags.stream().allMatch(tags::containsKey));
        assertTrue(csvTags.stream().noneMatch(fields::containsKey));
        assertTrue(csvFields.stream().allMatch(fields::containsKey));
        assertEquals(1234567e6, time.longValue()); // reads from file as millis, stores as nano
    }

    @Test
    public void parseFileWhenNoHeadersInDetailsReadsFirstRowAsHeaders() throws IOException {
        var file = FilesUtils.getFileFromResources("/csv/csv_with_header_row.csv");
        var details = new CsvUploadDetails(csvTags, new ObjectId(), new ObjectId(), "anything", null,
                                           FieldTypes.empty()
        );
        var result = csvLogsParser.parseFile(file, details, "anything");
        var points = result.getPoints();
        assertEquals(numberOfValueRows, points.size());
        assertEquals(Set.copyOf(csvTags), PointTestUtils.getTags(points.get(0)).keySet());
        assertEquals(Set.copyOf(csvFields), PointTestUtils.getFields(points.get(0)).keySet());
    }

    @Test
    public void parseFileWhenHeadersProvidedInDetailsReadsFirstRowAsValues() throws IOException {
        var file = FilesUtils.getFileFromResources("/csv/csv_without_header_row.csv");
        var details = new CsvUploadDetails(csvTags, new ObjectId(), new ObjectId(), "anything", csvHeaders,
                                           FieldTypes.empty()
        );
        var result = csvLogsParser.parseFile(file, details, "anything");
        var points = result.getPoints();
        assertEquals(numberOfValueRows, points.size());
        assertEquals(Set.copyOf(csvTags), PointTestUtils.getTags(points.get(0)).keySet());
        assertEquals(Set.copyOf(csvFields), PointTestUtils.getFields(points.get(0)).keySet());
    }


}
