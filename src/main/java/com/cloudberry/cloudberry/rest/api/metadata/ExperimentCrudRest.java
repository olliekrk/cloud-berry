package com.cloudberry.cloudberry.rest.api.metadata;

import com.cloudberry.cloudberry.db.mongo.data.metadata.Experiment;
import com.cloudberry.cloudberry.db.mongo.service.ExperimentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/metadata/experiment", consumes = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ExperimentCrudRest {
    private final ExperimentService experimentService;

    @GetMapping(value = "/all")
    List<Experiment> getAll() {
        return experimentService.findAll();
    }

    @GetMapping(value = "/byName")
    List<Experiment> getByName(@RequestParam String name) {
        return experimentService.findByName(name);
    }
}
