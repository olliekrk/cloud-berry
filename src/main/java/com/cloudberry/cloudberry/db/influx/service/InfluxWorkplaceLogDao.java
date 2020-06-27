package com.cloudberry.cloudberry.db.influx.service;

import com.cloudberry.cloudberry.db.influx.measurement.WorkplaceMeasurement;
import com.cloudberry.cloudberry.model.log.WorkplaceLog;
import com.cloudberry.cloudberry.service.dao.WorkplaceLogDao;
import com.cloudberry.cloudberry.util.syntax.ListSyntax;
import com.influxdb.client.InfluxDBClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Profile("influx")
public class InfluxWorkplaceLogDao implements WorkplaceLogDao {
    @Value("${spring.influx2.bucket}")
    private String logsBucketName;
    private final InfluxDBClient influxClient;
    private final Converter<WorkplaceMeasurement, WorkplaceLog> converter;

    @Override
    public List<WorkplaceLog> getByEvaluationId(UUID evaluationId) {
        var flux = String.format(
                "from(bucket:\"%s\") |> range(start:0) |> filter(fn: (r) => r.evaluationId == \"%s\")",
                logsBucketName,
                evaluationId.toString()
        );
        var result = influxClient.getQueryApi().query(flux, WorkplaceMeasurement.class);
        return ListSyntax.mapped(result, converter::convert);
    }

    @Override
    public List<WorkplaceLog> getByEvaluationIds(List<UUID> evaluationIds) {
        // todo: influx dao implementation
        return Collections.emptyList();
    }
}
