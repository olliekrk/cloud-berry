package com.cloudberry.cloudberry.processing.extractors;

import com.cloudberry.cloudberry.converters.EventToLogConverter;
import com.cloudberry.cloudberry.model.event.BestSolutionEvent;
import com.cloudberry.cloudberry.model.event.SummaryEvent;
import com.cloudberry.cloudberry.model.event.WorkplaceEvent;
import com.cloudberry.cloudberry.repository.facades.LogsSaver;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class LogsExtractor {

    private final LogsSaver logsSaver;
    private final EventToLogConverter eventToLogConverter;

    public LogsExtractor(LogsSaver logsSaver, EventToLogConverter eventToLogConverter) {
        this.logsSaver = logsSaver;
        this.eventToLogConverter = eventToLogConverter;
    }

    @Async
    public void extractAndSave(WorkplaceEvent event) {
        var log = eventToLogConverter.convert(event);
        logsSaver.saveLog(log).subscribe();
    }

    @Async
    public void extractAndSave(SummaryEvent event) {
        var log = eventToLogConverter.convert(event);
        logsSaver.saveLog(log).subscribe();
    }

    @Async
    public void extractAndSave(BestSolutionEvent event) {
        var log = eventToLogConverter.convert(event);
        logsSaver.saveLog(log).subscribe();
    }

}
