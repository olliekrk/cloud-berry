package com.cloudberry.cloudberry.converters;

import com.cloudberry.cloudberry.model.event.BestSolutionEvent;
import com.cloudberry.cloudberry.model.event.Event;
import com.cloudberry.cloudberry.model.event.SummaryEvent;
import com.cloudberry.cloudberry.model.event.WorkplaceEvent;
import com.cloudberry.cloudberry.model.logs.BestSolutionLog;
import com.cloudberry.cloudberry.model.logs.Log;
import com.cloudberry.cloudberry.model.logs.SummaryLog;
import com.cloudberry.cloudberry.model.logs.WorkplaceLog;
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
