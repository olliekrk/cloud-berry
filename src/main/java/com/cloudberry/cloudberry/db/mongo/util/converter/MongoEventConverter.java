package com.cloudberry.cloudberry.db.mongo.util.converter;

import com.cloudberry.cloudberry.db.mongo.data.logs.MongoBestSolutionLog;
import com.cloudberry.cloudberry.db.mongo.data.logs.MongoLog;
import com.cloudberry.cloudberry.db.mongo.data.logs.MongoSummaryLog;
import com.cloudberry.cloudberry.db.mongo.data.logs.MongoWorkplaceLog;
import com.cloudberry.cloudberry.kafka.event.Event;
import com.cloudberry.cloudberry.kafka.event.logs.BestSolutionEvent;
import com.cloudberry.cloudberry.kafka.event.logs.SummaryEvent;
import com.cloudberry.cloudberry.kafka.event.logs.WorkplaceEvent;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class MongoEventConverter implements Converter<Event, MongoLog> {

    @Override
    public MongoLog convert(@NonNull Event source) {
        if (source instanceof WorkplaceEvent) {
            return MongoWorkplaceLog.ofEvent((WorkplaceEvent) source);
        } else if (source instanceof SummaryEvent) {
            return MongoSummaryLog.ofEvent((SummaryEvent) source);
        } else if (source instanceof BestSolutionEvent) {
            return MongoBestSolutionLog.ofEvent((BestSolutionEvent) source);
        } else {
            return null;
        }
    }
}
