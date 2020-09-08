package com.cloudberry.cloudberry.rest.api.metadata;

import com.cloudberry.cloudberry.db.mongo.data.metadata.ExperimentComputation;
import com.cloudberry.cloudberry.db.mongo.service.ExperimentComputationService;
import com.cloudberry.cloudberry.rest.exceptions.invalid.id.InvalidConfigurationIdException;
import com.cloudberry.cloudberry.rest.util.RestParametersUtil;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.bson.types.ObjectId;
import org.springframework.http.MediaType;
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
    List<ExperimentComputation> getAll() {
        return experimentComputationService.findAll();
    }

    @GetMapping(value = "/byConfigurationId")
    List<ExperimentComputation> getByConfigurationId(@RequestParam String configurationIdHex)
            throws InvalidConfigurationIdException {
        val configurationId = getConfigurationId(configurationIdHex);

        return experimentComputationService.findAllComputationsForConfigurationId(configurationId);
    }

    @PostMapping("/create")
    ExperimentComputation create(@RequestParam String configurationIdHex) throws InvalidConfigurationIdException {
        val configurationId = getConfigurationId(configurationIdHex);
        val now = Instant.now();
        val computation = new ExperimentComputation(configurationId, now);
        return experimentComputationService.getOrCreateComputation(computation).block();
    }

    private ObjectId getConfigurationId(@RequestParam String configurationIdHex)
            throws InvalidConfigurationIdException {
        return RestParametersUtil.getValidId(configurationIdHex)
                .orElseThrow(() -> new InvalidConfigurationIdException(List.of(configurationIdHex)));
    }
}
