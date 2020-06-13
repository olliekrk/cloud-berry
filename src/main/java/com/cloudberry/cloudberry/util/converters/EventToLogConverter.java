package com.cloudberry.cloudberry.util.converters;

import com.cloudberry.cloudberry.kafka.event.BestSolutionEvent;
import com.cloudberry.cloudberry.kafka.event.Event;
import com.cloudberry.cloudberry.kafka.event.SummaryEvent;
import com.cloudberry.cloudberry.kafka.event.WorkplaceEvent;
import com.cloudberry.cloudberry.db.mongo.data.logs.MongoBestSolutionLog;
import com.cloudberry.cloudberry.db.mongo.data.logs.MongoLog;
import com.cloudberry.cloudberry.db.mongo.data.logs.MongoSummaryLog;
import com.cloudberry.cloudberry.db.mongo.data.logs.MongoWorkplaceLog;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class EventToLogConverter {

    public Mono<MongoLog> toLog(Event event) {
        return Mono.justOrEmpty(switch (event.getType()) {
            case WORKPLACE -> MongoWorkplaceLog.ofEvent((WorkplaceEvent) event);
            case SUMMARY -> MongoSummaryLog.ofEvent((SummaryEvent) event);
            case BEST_SOLUTION -> MongoBestSolutionLog.ofEvent((BestSolutionEvent) event);
            default -> null;
        });
    }
}
