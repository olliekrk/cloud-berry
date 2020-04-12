package com.cloudberry.cloudberry.processing.extractors;

import com.cloudberry.cloudberry.model.event.BestSolutionEvent;
import com.cloudberry.cloudberry.model.event.SummaryEvent;
import com.cloudberry.cloudberry.model.event.WorkplaceEvent;
import com.cloudberry.cloudberry.model.logs.BestSolutionLog;
import com.cloudberry.cloudberry.model.logs.Log;
import com.cloudberry.cloudberry.model.logs.SummaryLog;
import com.cloudberry.cloudberry.model.logs.WorkplaceLog;
import com.cloudberry.cloudberry.repository.facades.LogsRepositoryFacade;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class LogsExtractor {

    private final LogsRepositoryFacade logsRepositoryFacade;

    public LogsExtractor(LogsRepositoryFacade logsRepositoryFacade) {
        this.logsRepositoryFacade = logsRepositoryFacade;
    }

    @Async
    public void extractAndSave(WorkplaceEvent event) {
        var log = new WorkplaceLog(event.time, event.evaluationId, event.workplaceId, event.parameters);
        saveLog(log);
    }

    @Async
    public void extractAndSave(SummaryEvent event) {
        var log = new SummaryLog(event.time, event.evaluationId, event.bestEvaluation, event.evaluationsCount);
        saveLog(log);
    }

    @Async
    public void extractAndSave(BestSolutionEvent event) {
        var log = new BestSolutionLog(event.time, event.evaluationId, event.solution, event.details);
        saveLog(log);
    }

    private void saveLog(Log log) {
        logsRepositoryFacade.saveLog(log).subscribe();
    }
}
