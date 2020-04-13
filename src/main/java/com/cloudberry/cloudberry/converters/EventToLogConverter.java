package com.cloudberry.cloudberry.converters;

import com.cloudberry.cloudberry.model.event.BestSolutionEvent;
import com.cloudberry.cloudberry.model.event.SummaryEvent;
import com.cloudberry.cloudberry.model.event.WorkplaceEvent;
import com.cloudberry.cloudberry.model.logs.BestSolutionLog;
import com.cloudberry.cloudberry.model.logs.SummaryLog;
import com.cloudberry.cloudberry.model.logs.WorkplaceLog;

public interface EventToLogConverter {
    SummaryLog convert(SummaryEvent summaryEvent);

    WorkplaceLog convert(WorkplaceEvent workplaceEvent);

    BestSolutionLog convert(BestSolutionEvent bestSolutionEvent);
}
