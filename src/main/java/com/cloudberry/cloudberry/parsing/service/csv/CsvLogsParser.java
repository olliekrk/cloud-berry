package com.cloudberry.cloudberry.parsing.service.csv;

import com.cloudberry.cloudberry.db.influx.InfluxDefaults;
import com.cloudberry.cloudberry.parsing.model.ParsedLogs;
import com.cloudberry.cloudberry.parsing.model.csv.CsvUploadDetails;
import com.cloudberry.cloudberry.parsing.service.LogsParser;
import com.cloudberry.cloudberry.util.syntax.ListSyntax;
import com.influxdb.client.write.Point;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.lang3.tuple.Pair;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CsvLogsParser implements LogsParser<CsvUploadDetails> {
    @Value("${influx.measurements.default-measurement-name}")
    private String defaultMeasurementName;
    private static final CSVFormat CSV_FORMAT = CSVFormat.DEFAULT.withFirstRecordAsHeader();

    @Override
    public ParsedLogs parseFile(File file, CsvUploadDetails details) throws IOException {
        var measurementName = Optional.ofNullable(details.getMeasurementName()).orElse(defaultMeasurementName);
        var computationId = details.getComputationId().toHexString();

        Function<Map<String, String>, Map<String, String>> getTags =
                values -> details.getTagsNames()
                        .stream()
                        .flatMap(tag -> Optional.ofNullable(values.get(tag)).map(tagValue -> Pair.of(tag, tagValue)).stream())
                        .collect(Collectors.toMap(Pair::getKey, Pair::getValue));
        Function<Map<String, String>, Map<String, Object>> getFields =
                values -> values.entrySet()
                        .stream()
                        .filter(entry -> !Objects.equals(entry.getKey(), TIME_COLUMN_NAME) && !details.getTagsNames().contains(entry.getKey()))
                        .map(entry -> Pair.of(entry.getKey(), parseField(entry.getValue())))
                        .collect(Collectors.toMap(Pair::getKey, Pair::getValue));

        try (var reader = new BufferedReader(new FileReader(file));
             var parser = CSV_FORMAT.parse(reader)) {
            var points = ListSyntax.mapped(parser.getRecords(), record -> {
                var recordValues = record.toMap();
                return Point.measurement(measurementName)
                        .time(parseTime(recordValues.get(TIME_COLUMN_NAME)), InfluxDefaults.WRITE_PRECISION)
                        .addTags(getTags.apply(recordValues))
                        .addTag(InfluxDefaults.CommonTags.COMPUTATION_ID, computationId)
                        .addFields(getFields.apply(recordValues));
            });

            log.info("Successfully read " + points.size() + " data row(s) from CSV file");
            return new ParsedLogs(points);
        }
    }

}
