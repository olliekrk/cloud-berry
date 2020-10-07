package com.cloudberry.cloudberry.rest.api.metadata;

import com.cloudberry.cloudberry.db.mongo.data.metadata.Experiment;
import com.cloudberry.cloudberry.db.mongo.service.ExperimentService;
import com.cloudberry.cloudberry.rest.api.IdDispatcher;
import com.cloudberry.cloudberry.rest.exceptions.invalid.id.InvalidExperimentIdException;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping(value = CrudRestConst.ENDPOINT_PREFIX + "/experiment", consumes = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ExperimentCrudRest {
    private final ExperimentService experimentService;

    @GetMapping("/all")
    List<Experiment> getAll() {
        return experimentService.findAll();
    }

    @GetMapping("/byName")
    List<Experiment> getByName(@RequestParam String name) {
        return experimentService.findByName(name);
    }

    @PostMapping("/getOrCreate")
    Experiment getOrCreate(@RequestParam String name,
                           @RequestBody(required = false) Map<String, Object> parameters) {
        val now = Instant.now();
        val experimentParameters = Optional.ofNullable(parameters).orElse(Map.of());
        val experiment = new Experiment(now, name, experimentParameters);
        return experimentService.getOrCreateExperiment(experiment).block();
    }

    @PutMapping("/update")
    Experiment update(@RequestParam String experimentIdHex,
                      @RequestParam(required = false) String name,
                      @RequestParam(defaultValue = "false") boolean overrideParams,
                      @RequestBody(required = false) Map<String, Object> parameters
    ) throws InvalidExperimentIdException {
        val experimentId = IdDispatcher.getExperimentId(experimentIdHex);

        return experimentService.update(experimentId, name, parameters, overrideParams);
    }

    @DeleteMapping("/deleteById")
    void deleteComputation(@RequestParam String experimentIdHex) throws InvalidExperimentIdException {
        val experimentId = IdDispatcher.getExperimentId(experimentIdHex);

        experimentService.deleteById(experimentId);
    }
}
