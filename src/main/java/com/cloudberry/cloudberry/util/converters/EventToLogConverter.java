package com.cloudberry.cloudberry.util.converters;

import com.cloudberry.cloudberry.kafka.event.BestSolutionEvent;
import com.cloudberry.cloudberry.kafka.event.Event;
import com.cloudberry.cloudberry.kafka.event.SummaryEvent;
import com.cloudberry.cloudberry.kafka.event.WorkplaceEvent;
import com.cloudberry.cloudberry.db.mongo.data.logs.BestSolutionLog;
import com.cloudberry.cloudberry.db.mongo.data.logs.Log;
import com.cloudberry.cloudberry.db.mongo.data.logs.SummaryLog;
import com.cloudberry.cloudberry.db.mongo.data.logs.WorkplaceLog;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class EventToLogConverter {

    public Mono<Log> toLog(Event event) {
        return Mono.justOrEmpty(switch (event.getType()) {
            case WORKPLACE -> WorkplaceLog.ofEvent((WorkplaceEvent) event);
            case SUMMARY -> SummaryLog.ofEvent((SummaryEvent) event);
            case BEST_SOLUTION -> BestSolutionLog.ofEvent((BestSolutionEvent) event);
            default -> null;
        });
    }
}
