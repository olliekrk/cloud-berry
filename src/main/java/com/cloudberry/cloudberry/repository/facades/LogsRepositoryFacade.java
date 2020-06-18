package com.cloudberry.cloudberry.repository.facades;

import com.cloudberry.cloudberry.db.mongo.data.logs.MongoBestSolutionLog;
import com.cloudberry.cloudberry.db.mongo.data.logs.MongoLog;
import com.cloudberry.cloudberry.db.mongo.data.logs.MongoSummaryLog;
import com.cloudberry.cloudberry.db.mongo.data.logs.MongoWorkplaceLog;
import com.cloudberry.cloudberry.kafka.event.EventType;
import com.cloudberry.cloudberry.repository.BestSolutionLogsRepository;
import com.cloudberry.cloudberry.repository.SummaryLogsRepository;
import com.cloudberry.cloudberry.repository.WorkplaceLogsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class LogsRepositoryFacade {
    private final WorkplaceLogsRepository workplaceLogsRepository;
    private final SummaryLogsRepository summaryLogsRepository;
    private final BestSolutionLogsRepository bestSolutionLogsRepository;

    public Mono<? extends MongoLog> save(MongoLog log) {
        return switch (log.getType()) {
            case WORKPLACE -> workplaceLogsRepository.save((MongoWorkplaceLog) log);
            case SUMMARY -> summaryLogsRepository.save((MongoSummaryLog) log);
            case BEST_SOLUTION -> bestSolutionLogsRepository.save((MongoBestSolutionLog) log);
            default -> Mono.empty();
        };
    }

    //this is probably really bad todo fix it
    public Flux<? extends MongoLog> saveAll(List<MongoLog> logs, EventType eventType) {
        return switch (eventType) {
            case WORKPLACE -> workplaceLogsRepository.saveAll((List<MongoWorkplaceLog>) (Object) logs);
            case SUMMARY -> summaryLogsRepository.saveAll((List<MongoSummaryLog>) (Object) logs);
            case BEST_SOLUTION -> bestSolutionLogsRepository.saveAll((List<MongoBestSolutionLog>) (Object) logs);
            default -> Flux.empty();
        };
    }
}
