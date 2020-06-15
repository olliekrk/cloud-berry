package com.cloudberry.cloudberry.db.mongo.service;

import com.cloudberry.cloudberry.db.mongo.data.logs.MongoWorkplaceLog;
import com.cloudberry.cloudberry.model.log.WorkplaceLog;
import com.cloudberry.cloudberry.repository.WorkplaceLogsRepository;
import com.cloudberry.cloudberry.service.dao.WorkplaceLogsDao;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Profile("mongo")
public class MongoWorkplaceLogsDao implements WorkplaceLogsDao {

    private final Converter<MongoWorkplaceLog, WorkplaceLog> converter;
    private final WorkplaceLogsRepository workplaceLogsRepository;

    @Override
    public List<WorkplaceLog> getByEvaluationId(UUID evaluationId) {
        return converted(workplaceLogsRepository.findAllByEvaluationId(evaluationId));
    }

    @Override
    public List<WorkplaceLog> getByEvaluationIdAndWorkplaceId(UUID evaluationId, long workplaceId) {
        return converted(workplaceLogsRepository.findAllByEvaluationIdAndWorkplaceId(evaluationId, workplaceId));
    }

    @Override
    public List<WorkplaceLog> getByEvaluationIds(List<UUID> evaluationIds) {
        return converted(workplaceLogsRepository.findAllByEvaluationIdIn(evaluationIds));
    }

    private List<WorkplaceLog> converted(Flux<MongoWorkplaceLog> logs) {
        return logs.toStream().map(converter::convert).collect(Collectors.toList());
    }
}
