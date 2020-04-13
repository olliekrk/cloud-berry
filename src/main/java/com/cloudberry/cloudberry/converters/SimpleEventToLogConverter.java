package com.cloudberry.cloudberry.converters;

import com.cloudberry.cloudberry.model.event.BestSolutionEvent;
import com.cloudberry.cloudberry.model.event.SummaryEvent;
import com.cloudberry.cloudberry.model.event.WorkplaceEvent;
import com.cloudberry.cloudberry.model.logs.BestSolutionLog;
import com.cloudberry.cloudberry.model.logs.SummaryLog;
import com.cloudberry.cloudberry.model.logs.WorkplaceLog;
import org.springframework.stereotype.Component;

@Component
public class SimpleEventToLogConverter implements EventToLogConverter {

    @Override
    public SummaryLog convert(SummaryEvent summaryEvent) {
        return new SummaryLog(summaryEvent.getTime(),
                summaryEvent.getEvaluationId(),
                summaryEvent.getBestEvaluation(),
                summaryEvent.getEvaluationsCount());
    }

    @Override
    public WorkplaceLog convert(WorkplaceEvent workplaceEvent) {
        return new WorkplaceLog(workplaceEvent.getTime(),
                workplaceEvent.getEvaluationId(),
                workplaceEvent.getWorkplaceId(),
                workplaceEvent.getParameters());
    }

    @Override
    public BestSolutionLog convert(BestSolutionEvent bestSolutionEvent) {
        return new BestSolutionLog(bestSolutionEvent.getTime(),
                bestSolutionEvent.getEvaluationId(),
                bestSolutionEvent.getSolution(),
                bestSolutionEvent.getDetails());
    }
}
