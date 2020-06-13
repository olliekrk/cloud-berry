package com.cloudberry.cloudberry.db.influx.service;

import com.cloudberry.cloudberry.model.log.WorkplaceLog;
import com.cloudberry.cloudberry.service.dao.WorkplaceLogsDao;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Profile("influx")
public class InfluxWorkplaceLogsDao implements WorkplaceLogsDao {

    // todo: influx dao methods implementation

    @Override
    public List<WorkplaceLog> getByEvaluationId(UUID evaluationId) {
        return Collections.emptyList();
    }

    @Override
    public List<WorkplaceLog> getByEvaluationIdAndWorkplaceId(UUID evaluationId, long workplaceId) {
        return Collections.emptyList();
    }

    @Override
    public List<WorkplaceLog> getByEvaluationIds(List<UUID> evaluationIds) {
        return Collections.emptyList();
    }
}
