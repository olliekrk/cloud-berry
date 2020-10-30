package com.cloudberry.cloudberry.parsing.service.csv;

import com.cloudberry.cloudberry.common.syntax.ListSyntax;
import com.cloudberry.cloudberry.db.influx.InfluxDefaults;
import com.cloudberry.cloudberry.parsing.model.ParsedLogs;
import com.cloudberry.cloudberry.parsing.model.csv.CsvUploadDetails;
import com.cloudberry.cloudberry.parsing.service.LogsParser;
import com.influxdb.client.write.Point;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
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

    @Override
    public ParsedLogs parseFile(File file, CsvUploadDetails details, String defaultMeasurementName) throws IOException {
        var measurementName = Optional.ofNullable(details.getMeasurementName()).orElse(defaultMeasurementName);

        return Try.withResources(() -> new BufferedReader(new FileReader(file)))
                .of(reader -> Try.withResources(() -> details.determineCsvFormat().parse(reader))
                        .of(parser -> {
                            var points = ListSyntax.flatMapped(parser.getRecords(), record -> {
                                var recordMap = record.toMap();
                                var recordTime = parseTime(recordMap.get(TIME_COLUMN_NAME));
                                var recordTags = getTags(details, recordMap);
                                var recordFields = getFields(details, recordMap);

                                return recordFields.entrySet().stream()
                                        .map(recordField -> Point
                                                .measurement(measurementName)
                                                .time(recordTime, InfluxDefaults.WRITE_PRECISION)
                                                .addTags(recordTags)
                                                .addField(InfluxDefaults.Columns.FIELD, recordField.getKey())
                                                .addFields(Map.ofEntries(recordField))
                                        )
                                        .collect(Collectors.toList());
                            });

                            log.info("Successfully read " + points.size() + " data row(s) from CSV file");
                            return new ParsedLogs(points);
                        })
                        .getOrElseThrow((Function<Throwable, IOException>) IOException::new))
                .getOrElseThrow((Function<Throwable, IOException>) IOException::new);
    }

    @NotNull
    private Map<String, String> getTags(CsvUploadDetails details, Map<String, String> values) {
        return details.getTagsNames()
                .stream()
                .flatMap(tag ->
                                 Optional.ofNullable(values.get(tag))
                                         .map(tagValue -> Pair.of(tag, tagValue))
                                         .stream()
                )
                .collect(Collectors.toMap(Pair::getKey, Pair::getValue));
    }

    @NotNull
    private Map<String, Object> getFields(CsvUploadDetails details, Map<String, String> values) {
        return values.entrySet()
                .stream()
                .filter(entry ->
                                !Objects.equals(entry.getKey(), TIME_COLUMN_NAME) &&
                                        !details.getTagsNames().contains(entry.getKey())
                )
                .map(entry -> Pair.of(entry.getKey(), parseField(entry.getValue())))
                .collect(Collectors.toMap(Pair::getKey, Pair::getValue));
    }

}
