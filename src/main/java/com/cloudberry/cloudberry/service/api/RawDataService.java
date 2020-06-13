package com.cloudberry.cloudberry.service.api;

import com.cloudberry.cloudberry.model.log.WorkplaceLog;
import com.cloudberry.cloudberry.service.dao.WorkplaceLogsDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RawDataService {

    private final WorkplaceLogsDao workplaceLogsDao;

    public List<WorkplaceLog> getByEvaluationId(UUID evaluationId) {
        return workplaceLogsDao.getByEvaluationId(evaluationId);
    }

    public List<WorkplaceLog> getByEvaluationIdAndWorkplaceId(UUID evaluationId, long workplaceId) {
        return workplaceLogsDao.getByEvaluationIdAndWorkplaceId(evaluationId, workplaceId);
    }

    public List<WorkplaceLog> getByEvaluationIds(List<UUID> evaluationIds) {
        return workplaceLogsDao.getByEvaluationIds(evaluationIds);
    }

}
