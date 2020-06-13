package com.cloudberry.cloudberry.db.mongo.util.converter;

import com.cloudberry.cloudberry.db.mongo.data.logs.MongoWorkplaceLog;
import com.cloudberry.cloudberry.model.log.WorkplaceLog;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
public class MongoLogsConverter implements Converter<MongoWorkplaceLog, WorkplaceLog> {

    @Override
    public WorkplaceLog convert(@Nullable MongoWorkplaceLog source) {
        if (source == null) {
            return null;
        } else {
            return new WorkplaceLog(
                    source.getTime(),
                    source.getEvaluationId(),
                    source.getWorkplaceId(),
                    source.getParameters()
            );
        }
    }
    
}
