package com.cloudberry.cloudberry.service.api;

import com.cloudberry.cloudberry.db.influx.data.PointBuilder;
import com.cloudberry.cloudberry.db.influx.service.InfluxDataAccessor;
import com.cloudberry.cloudberry.db.influx.service.InfluxDataEvictor;
import com.cloudberry.cloudberry.db.influx.service.InfluxDataWriter;
import com.cloudberry.cloudberry.model.statistics.DataPoint;
import com.cloudberry.cloudberry.model.statistics.DataSeries;
import com.cloudberry.cloudberry.rest.dto.DataFilters;
import com.cloudberry.cloudberry.util.syntax.ListSyntax;
import com.influxdb.query.FluxRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RawDataService {
    private final PointBuilder pointBuilder;
    private final InfluxDataWriter influxDataWriter;
    private final InfluxDataEvictor influxDataEvictor;
    private final InfluxDataAccessor influxDataAccessor;
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
}
