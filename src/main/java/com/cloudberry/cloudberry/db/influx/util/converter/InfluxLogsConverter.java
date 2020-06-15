package com.cloudberry.cloudberry.db.influx.util.converter;

import com.cloudberry.cloudberry.db.influx.data.WorkplaceLogMeasurement;
import com.cloudberry.cloudberry.model.log.WorkplaceLog;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.UUID;

@Component
public class InfluxLogsConverter implements Converter<WorkplaceLogMeasurement, WorkplaceLog> {
    @Override
    public WorkplaceLog convert(WorkplaceLogMeasurement source) {
        return new WorkplaceLog(
                source.getTime(),
                UUID.fromString(source.getEvaluationId()),
                source.getWorkplaceId(),
                Collections.emptyMap()
        );
    }
}
