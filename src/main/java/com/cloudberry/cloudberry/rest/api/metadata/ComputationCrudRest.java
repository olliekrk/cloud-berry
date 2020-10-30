package com.cloudberry.cloudberry.rest.api.metadata;

import com.cloudberry.cloudberry.db.mongo.data.metadata.ExperimentComputation;
import com.cloudberry.cloudberry.db.mongo.service.computation.ExperimentComputationService;
import com.cloudberry.cloudberry.rest.exceptions.invalid.id.InvalidComputationIdException;
import com.cloudberry.cloudberry.rest.exceptions.invalid.id.InvalidConfigurationIdException;
import com.cloudberry.cloudberry.rest.exceptions.invalid.id.InvalidExperimentIdException;
import com.cloudberry.cloudberry.rest.util.IdDispatcher;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.bson.types.ObjectId;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping(value = CrudRestConst.ENDPOINT_PREFIX + "/computation", consumes = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ComputationCrudRest {
    private final ExperimentComputationService experimentComputationService;

    @GetMapping(value = "/all")
    List<ExperimentComputation> findAll() {
        return experimentComputationService.findAll();
    }

    @GetMapping(value = "/byId")
    ExperimentComputation findById(@RequestParam String computationIdHex) throws InvalidComputationIdException {
        val computationId = IdDispatcher.getComputationId(computationIdHex);

        return experimentComputationService.findById(computationId);
    }

    @GetMapping(value = "/byConfigurationId")
    List<ExperimentComputation> findByConfigurationId(@RequestParam String configurationIdHex)
            throws InvalidConfigurationIdException {
        val configurationId = IdDispatcher.getConfigurationId(configurationIdHex);

        return experimentComputationService.findByConfigurationId(configurationId);
    }

    @GetMapping(value = "/byExperimentId")
    List<ExperimentComputation> findByExperimentId(@RequestParam String experimentIdHex)
            throws InvalidExperimentIdException {
        val experimentId = IdDispatcher.getExperimentId(experimentIdHex);

        return experimentComputationService.findByExperimentId(experimentId);
    }

    @PostMapping("/create")
    ExperimentComputation create(@RequestParam String configurationIdHex) throws InvalidConfigurationIdException {
        val configurationId = IdDispatcher.getConfigurationId(configurationIdHex);
        val now = Instant.now();
        val computation = new ExperimentComputation(ObjectId.get(), configurationId, now);
        return experimentComputationService.getOrCreateComputation(computation);
    }

    @DeleteMapping("/deleteById")
    void deleteComputation(@RequestParam String computationIdHex) throws InvalidComputationIdException {
        val computationId = IdDispatcher.getComputationId(computationIdHex);

        experimentComputationService.deleteById(computationId);
    }

}
