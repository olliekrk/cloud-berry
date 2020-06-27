package com.cloudberry.cloudberry.db.influx.util.converter;

import com.cloudberry.cloudberry.db.influx.measurement.WorkplaceMeasurement;
import com.cloudberry.cloudberry.model.log.WorkplaceLog;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.UUID;

@Component
public class InfluxLogsConverter implements Converter<WorkplaceMeasurement, WorkplaceLog> {
    @Override
    public WorkplaceLog convert(WorkplaceMeasurement source) {
        return new WorkplaceLog(
                source.getTime(),
                UUID.fromString(source.getEvaluationId()),
                source.getWorkplaceId(),
                Collections.emptyMap()
        );
    }
}
