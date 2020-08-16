package com.cloudberry.cloudberry.service.api;

import com.cloudberry.cloudberry.parsing.model.age.AgeUploadDetails;
import com.cloudberry.cloudberry.parsing.model.csv.CsvUploadDetails;
import com.cloudberry.cloudberry.parsing.service.LogsImporter;
import com.cloudberry.cloudberry.db.influx.data.PointBuilder;
import com.cloudberry.cloudberry.db.influx.service.InfluxDataAccessor;
import com.cloudberry.cloudberry.db.influx.service.InfluxDataEvictor;
import com.cloudberry.cloudberry.db.influx.service.InfluxDataWriter;
import com.cloudberry.cloudberry.model.statistics.DataPoint;
import com.cloudberry.cloudberry.model.statistics.DataSeries;
import com.cloudberry.cloudberry.rest.dto.DataFilters;
import com.cloudberry.cloudberry.rest.exceptions.FileImportException;
import com.cloudberry.cloudberry.util.FileSystemUtils;
import com.cloudberry.cloudberry.util.syntax.ListSyntax;
import com.influxdb.query.FluxRecord;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RawDataService {
    private final PointBuilder pointBuilder;
    private final InfluxDataWriter influxDataWriter;
    private final InfluxDataEvictor influxDataEvictor;
    private final InfluxDataAccessor influxDataAccessor;
    private final LogsImporter logsImporter;
    @Value("${influx.measurements.default-measurement-name}")
    private String defaultMeasurementName;
    public static final String rawDataSeriesName = "raw_data";

    public void saveData(@Nullable String bucketName,
                         @Nullable String measurementNameOpt,
                         List<DataPoint> dataPoints) {
        var measurementName = Optional.ofNullable(measurementNameOpt).orElse(defaultMeasurementName);
        var influxDataPoints = ListSyntax.mapped(dataPoints,
                p -> pointBuilder.buildPoint(measurementName, p.getTime(), p.getFields(), p.getTags())
        );

        log.info(String.format("Saving %d data points to the DB with measurement name: %s", influxDataPoints.size(), measurementName));
        influxDataWriter.writePoints(bucketName, influxDataPoints);
    }

    public DataSeries findData(@Nullable String bucketName,
                               @Nullable String measurementName,
                               DataFilters filters) {
        var records = influxDataAccessor.findData(
                bucketName,
                measurementName,
                filters.getFieldFilters(),
                filters.getTagFilters()
        );

        var data = ListSyntax.mapped(records, FluxRecord::getValues);
        return new DataSeries(rawDataSeriesName, data);
    }

    public void deleteData(@Nullable String bucketName,
                           @Nullable String measurementName,
                           DataFilters filters) {
        influxDataEvictor.deleteData(
                bucketName,
                measurementName,
                filters.getTagFilters()
        );
    }

    public ObjectId uploadAgeFile(MultipartFile file, String experimentName, AgeUploadDetails uploadDetails) {
        return Try.of(() -> FileSystemUtils.withTemporaryFile(file, tmpFilePath ->
                Try.of(() -> logsImporter
                        .importAgeFile(tmpFilePath.toFile(), experimentName, uploadDetails))
                        .getOrElseThrow(FileImportException::new)))
                .getOrElseThrow(FileImportException::new);
    }

    public ObjectId uploadCsvFile(MultipartFile file, String experimentName, CsvUploadDetails uploadDetails) {
        return Try.of(() -> FileSystemUtils.withTemporaryFile(file, tmpFilePath ->
                Try.of(() -> logsImporter
                        .importCsvFile(tmpFilePath.toFile(), experimentName, uploadDetails))
                        .getOrElseThrow(FileImportException::new)))
                .getOrElseThrow(FileImportException::new);
    }
}
