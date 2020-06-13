package com.cloudberry.cloudberry.repository.facades;

import com.cloudberry.cloudberry.db.mongo.data.logs.BestSolutionLog;
import com.cloudberry.cloudberry.db.mongo.data.logs.Log;
import com.cloudberry.cloudberry.db.mongo.data.logs.SummaryLog;
import com.cloudberry.cloudberry.db.mongo.data.logs.WorkplaceLog;
import com.cloudberry.cloudberry.repository.BestSolutionLogsRepository;
import com.cloudberry.cloudberry.repository.SummaryLogsRepository;
import com.cloudberry.cloudberry.repository.WorkplaceLogsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class LogsRepositoryFacade
{
    private final WorkplaceLogsRepository workplaceLogsRepository;
    private final SummaryLogsRepository summaryLogsRepository;
    private final BestSolutionLogsRepository bestSolutionLogsRepository;

    public Mono<? extends Log> save(Log log) {
        return switch (log.getType()) {
            case WORKPLACE -> workplaceLogsRepository.save((WorkplaceLog) log);
            case SUMMARY -> summaryLogsRepository.save((SummaryLog) log);
            case BEST_SOLUTION -> bestSolutionLogsRepository.save((BestSolutionLog) log);
            default -> Mono.empty();
        };
    }
}
