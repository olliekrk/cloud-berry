package com.cloudberry.cloudberry.service.dao;

import com.cloudberry.cloudberry.model.log.WorkplaceLog;

import java.util.List;
import java.util.UUID;

public interface WorkplaceLogsDao {

    List<WorkplaceLog> getByEvaluationId(UUID evaluationId);

    List<WorkplaceLog> getByEvaluationIdAndWorkplaceId(UUID evaluationId, long workplaceId);

    List<WorkplaceLog> getByEvaluationIds(List<UUID> evaluationIds);

}
