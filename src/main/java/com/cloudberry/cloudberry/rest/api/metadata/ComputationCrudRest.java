package com.cloudberry.cloudberry.rest.api.metadata;

import com.cloudberry.cloudberry.db.mongo.data.metadata.ExperimentComputation;
import com.cloudberry.cloudberry.db.mongo.data.metadata.ExperimentConfiguration;
import com.cloudberry.cloudberry.db.mongo.service.ComputationService;
import com.cloudberry.cloudberry.rest.exceptions.invalid.id.InvalidExperimentIdException;
import com.cloudberry.cloudberry.rest.util.RestParametersUtil;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping(value = "/metadata/computation", consumes = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ComputationCrudRest {
    private final ComputationService computationService;

    @GetMapping(value = "/all")
    List<ExperimentComputation> getAll() {
        return computationService.findAll();
    }
}
