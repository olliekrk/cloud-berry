package com.cloudberry.cloudberry.db.influx.util.converter;

import com.cloudberry.cloudberry.db.influx.measurement.WorkplaceMeasurement;
import com.cloudberry.cloudberry.model.log.WorkplaceLog;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class InfluxLogsConverter implements Converter<WorkplaceMeasurement, WorkplaceLog> {
    @Override
    public WorkplaceLog convert(WorkplaceMeasurement source) {
        return new WorkplaceLog(
                source.getTime(),
                source.getEvaluationId(),
                source.getWorkplaceId(),
                Collections.emptyMap()
        );
    }
}
