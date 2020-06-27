package com.cloudberry.cloudberry.service.dao;

import com.cloudberry.cloudberry.model.log.WorkplaceLog;

import java.util.List;
import java.util.UUID;

public interface WorkplaceLogDao {

    List<WorkplaceLog> getByEvaluationId(UUID evaluationId);

    List<WorkplaceLog> getByEvaluationIds(List<UUID> evaluationIds);

}
