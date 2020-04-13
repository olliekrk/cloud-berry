package com.cloudberry.cloudberry.repository.facades;

import com.cloudberry.cloudberry.model.logs.BestSolutionLog;
import com.cloudberry.cloudberry.model.logs.Log;
import com.cloudberry.cloudberry.model.logs.SummaryLog;
import com.cloudberry.cloudberry.model.logs.WorkplaceLog;
import reactor.core.publisher.Mono;

public interface LogsSaver {
    Mono<? extends Log> saveLog(WorkplaceLog log);

    Mono<? extends Log> saveLog(SummaryLog log);

    Mono<? extends Log> saveLog(BestSolutionLog log);
}
