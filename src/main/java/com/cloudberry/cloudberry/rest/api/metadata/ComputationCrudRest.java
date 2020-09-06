package com.cloudberry.cloudberry.rest.api.metadata;

import com.cloudberry.cloudberry.db.mongo.data.metadata.ExperimentComputation;
import com.cloudberry.cloudberry.db.mongo.service.ComputationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = CrudRestConst.ENDPOINT_PREFIX + "/computation", consumes = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ComputationCrudRest {
    private final ComputationService computationService;

    @GetMapping(value = "/all")
    List<ExperimentComputation> getAll() {
        return computationService.findAll();
    }
}
