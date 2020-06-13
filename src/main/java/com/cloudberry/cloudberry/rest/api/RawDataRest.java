package com.cloudberry.cloudberry.rest.api;

import com.cloudberry.cloudberry.model.log.WorkplaceLog;
import com.cloudberry.cloudberry.service.api.RawDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/raw")
@RequiredArgsConstructor
@Slf4j
public class RawDataRest {

    private final RawDataService rawDataService;

    @GetMapping("/{evaluationId}")
    public List<WorkplaceLog> getByEvaluationId(@PathVariable UUID evaluationId) {
        return rawDataService.getByEvaluationId(evaluationId);
    }

    @GetMapping("/{evaluationId}/{workplaceId}")
    public List<WorkplaceLog> getByEvaluationIdAndWorkplaceId(@PathVariable UUID evaluationId,
                                                              @PathVariable long workplaceId) {
        return rawDataService.getByEvaluationIdAndWorkplaceId(evaluationId, workplaceId);
    }

    @PostMapping("/byEvaluationIds")
    public List<WorkplaceLog> getByEvaluationIds(@RequestBody List<UUID> evaluationIds) {
        return rawDataService.getByEvaluationIds(evaluationIds);
    }


}
