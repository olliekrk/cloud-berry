package com.cloudberry.cloudberry.db.influx.service;

import com.cloudberry.cloudberry.db.influx.data.WorkplaceLogMeasurement;
import com.cloudberry.cloudberry.model.log.WorkplaceLog;
import com.cloudberry.cloudberry.service.dao.WorkplaceLogsDao;
import com.cloudberry.cloudberry.util.syntax.ListSyntax;
import com.influxdb.client.InfluxDBClient;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Profile("influx")
public class InfluxWorkplaceLogsDao implements WorkplaceLogsDao {
    private final InfluxDBClient influxClient;
    private final Converter<WorkplaceLogMeasurement, WorkplaceLog> converter;

    @Override
    public List<WorkplaceLog> getByEvaluationId(UUID evaluationId) {
        var flux = String.format("from(bucket:\"cloudberry-logs\") |> range(start:0) |> filter(fn: (r) => r.evaluationId == %s)", evaluationId.toString());
        var result = influxClient.getQueryApi().query(flux, WorkplaceLogMeasurement.class);
        return ListSyntax.mapped(result, converter::convert);
    }

    @Override
    public List<WorkplaceLog> getByEvaluationIdAndWorkplaceId(UUID evaluationId, long workplaceId) {
        var flux = String.format("from(bucket:\"cloudberry-logs\") |> range(start:0) |> filter(fn: (r) => r.evaluationId == \"%s\" and r.workplaceId == %s)", evaluationId.toString(), workplaceId);
        var result = influxClient.getQueryApi().query(flux, WorkplaceLogMeasurement.class);
        return ListSyntax.mapped(result, converter::convert);
    }

    @Override
    public List<WorkplaceLog> getByEvaluationIds(List<UUID> evaluationIds) {
        // todo: influx dao implementation
        return Collections.emptyList();
    }
}
