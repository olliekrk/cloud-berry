package com.cloudberry.cloudberry.repository.facades;

import com.cloudberry.cloudberry.model.logs.BestSolutionLog;
import com.cloudberry.cloudberry.model.logs.Log;
import com.cloudberry.cloudberry.model.logs.SummaryLog;
import com.cloudberry.cloudberry.model.logs.WorkplaceLog;
import com.cloudberry.cloudberry.repository.BestSolutionLogsRepository;
import com.cloudberry.cloudberry.repository.SummaryLogsRepository;
import com.cloudberry.cloudberry.repository.WorkplaceLogsRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class LogsRepositoryFacade {
    private final WorkplaceLogsRepository workplaceLogsRepository;
    private final SummaryLogsRepository summaryLogsRepository;
    private final BestSolutionLogsRepository bestSolutionLogsRepository;

    public LogsRepositoryFacade(WorkplaceLogsRepository workplaceLogsRepository,
                                SummaryLogsRepository summaryLogsRepository,
                                BestSolutionLogsRepository bestSolutionLogsRepository) {
        this.workplaceLogsRepository = workplaceLogsRepository;
        this.summaryLogsRepository = summaryLogsRepository;
        this.bestSolutionLogsRepository = bestSolutionLogsRepository;
    }

    public Mono<? extends Log> saveLog(Log log) {
        if (log instanceof WorkplaceLog) {
            return workplaceLogsRepository.save((WorkplaceLog) log);
        } else if (log instanceof SummaryLog) {
            return summaryLogsRepository.save((SummaryLog) log);
        } else if (log instanceof BestSolutionLog) {
            return bestSolutionLogsRepository.save((BestSolutionLog) log);
        } else {
            return Mono.empty();
        }
    }
}
