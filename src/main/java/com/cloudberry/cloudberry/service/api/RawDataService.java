package com.cloudberry.cloudberry.service.api;

import com.cloudberry.cloudberry.analytics.model.basic.DataPoint;
import com.cloudberry.cloudberry.analytics.model.basic.DataSeries;
import com.cloudberry.cloudberry.analytics.model.basic.SeriesInfo;
import com.cloudberry.cloudberry.analytics.model.filters.DataFilters;
import com.cloudberry.cloudberry.analytics.model.query.InfluxQueryFields;
import com.cloudberry.cloudberry.common.syntax.ListSyntax;
import com.cloudberry.cloudberry.config.influx.InfluxConfig;
import com.cloudberry.cloudberry.db.influx.data.PointBuilder;
import com.cloudberry.cloudberry.db.influx.service.InfluxDataAccessor;
import com.cloudberry.cloudberry.db.influx.service.InfluxDataRemover;
import com.cloudberry.cloudberry.db.influx.service.InfluxDataWriter;
import com.cloudberry.cloudberry.db.mongo.data.metadata.ExperimentComputation;
import com.cloudberry.cloudberry.parsing.model.age.AgeUploadDetails;
import com.cloudberry.cloudberry.parsing.model.csv.CsvUploadDetails;
import com.cloudberry.cloudberry.parsing.service.LogsImporter;
import com.cloudberry.cloudberry.rest.exceptions.FileImportException;
import com.cloudberry.cloudberry.util.FileSystemUtils;
import com.influxdb.query.FluxRecord;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static java.lang.String.format;

@Slf4j
@Service
@RequiredArgsConstructor
public class RawDataService {
    private final PointBuilder pointBuilder;
    private final InfluxDataWriter influxDataWriter;
    private final InfluxDataRemover influxDataRemover;
    private final InfluxDataAccessor influxDataAccessor;
    private final LogsImporter logsImporter;
    private final InfluxConfig influxConfig;
    public static final String RAW_DATA_SERIES_NAME = "raw_data";

    public void saveData(
            InfluxQueryFields influxQueryFields,
            List<DataPoint> dataPoints
    ) {
        var measurementName =
                influxQueryFields.getMeasurementNameOptional().orElse(influxConfig.getDefaultMeasurementName());
        var influxDataPoints = ListSyntax.mapped(
                dataPoints,
                p -> pointBuilder.buildPoint(measurementName, p.getTime(), p.getFields(), p.getTags())
        );

        log.info(format("Saving %d data points to the DB with measurement name: %s", influxDataPoints.size(),
                        measurementName
        ));
        influxDataWriter.writePoints(influxQueryFields.getBucketName(), influxDataPoints);
    }

    public DataSeries findData(
            InfluxQueryFields influxQueryFields,
            DataFilters filters
    ) {
        var records = influxDataAccessor.findData(influxQueryFields, filters);
        var data = ListSyntax.mapped(records, FluxRecord::getValues);
        return new DataSeries(new SeriesInfo(RAW_DATA_SERIES_NAME), data);
    }

    public void deleteData(InfluxQueryFields influxQueryFields, DataFilters filters) {
        influxDataRemover.deleteData(influxQueryFields, filters.getTagFilters());
    }

    public ExperimentComputation uploadAgeFile(
            MultipartFile file, String experimentName, AgeUploadDetails uploadDetails
    ) {
        return Try.of(() -> FileSystemUtils.withTemporaryFile(file, tmpFilePath ->
                Try.of(() -> logsImporter
                        .importAgeFile(tmpFilePath.toFile(), experimentName, uploadDetails))
                        .getOrElseThrow(FileImportException::new)))
                .getOrElseThrow(FileImportException::new);
    }

    public ExperimentComputation uploadCsvFile(
            MultipartFile file, String experimentName, CsvUploadDetails uploadDetails
    ) {
        return Try.of(() -> FileSystemUtils.withTemporaryFile(file, tmpFilePath ->
                Try.of(() -> logsImporter
                        .importCsvFile(tmpFilePath.toFile(), experimentName, uploadDetails))
                        .getOrElseThrow(FileImportException::new)))
                .getOrElseThrow(FileImportException::new);
    }
}
