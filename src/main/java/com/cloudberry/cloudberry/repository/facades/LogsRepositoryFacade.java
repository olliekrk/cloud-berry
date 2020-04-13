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
public class LogsRepositoryFacade implements LogsSaver {
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

    @Override
    public Mono<? extends Log> saveLog(WorkplaceLog log) {
        return workplaceLogsRepository.save(log);
    }

    @Override
    public Mono<? extends Log> saveLog(SummaryLog log) {
        return summaryLogsRepository.save(log);
    }

    @Override
    public Mono<? extends Log> saveLog(BestSolutionLog log) {
        return bestSolutionLogsRepository.save(log);
    }
}
