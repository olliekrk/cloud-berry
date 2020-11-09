package com.cloudberry.cloudberry.rest.api.metadata;

import com.cloudberry.cloudberry.db.mongo.data.metadata.ExperimentConfiguration;
import com.cloudberry.cloudberry.db.mongo.service.configuration.ExperimentConfigurationService;
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
@RequestMapping(value = CrudRestConst.ENDPOINT_PREFIX + "/configuration", consumes = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ConfigurationCrudRest {
    private final ExperimentConfigurationService experimentConfigurationService;

    @GetMapping(value = "/all")
    List<ExperimentConfiguration> findAll() {
        return experimentConfigurationService.findAll();
    }

    @GetMapping(value = "/byId")
    ExperimentConfiguration findById(@RequestParam String configurationIdHex) throws InvalidConfigurationIdException {
        val configurationId = IdDispatcher.getConfigurationId(configurationIdHex);

        return experimentConfigurationService.findById(configurationId);
    }

    @GetMapping(value = "/byComputationId")
    ExperimentConfiguration findByComputationId(@RequestParam String computationIdHex)
            throws InvalidComputationIdException {
        val computationId = IdDispatcher.getComputationId(computationIdHex);

        return experimentConfigurationService.findByComputationId(computationId);
    }

    @GetMapping(value = "/byExperimentId")
    List<ExperimentConfiguration> findByExperimentId(@RequestParam String experimentIdHex)
            throws InvalidExperimentIdException {
        val experimentId = IdDispatcher.getExperimentId(experimentIdHex);

        return experimentConfigurationService.findByExperimentId(experimentId);
    }

    @GetMapping(value = "/byConfigurationName")
    List<ExperimentConfiguration> findByConfigurationName(@RequestParam String configurationName) {
        return experimentConfigurationService.findByConfigurationName(configurationName);
    }

    @GetMapping(value = "/byExperimentName")
    List<ExperimentConfiguration> findByExperimentName(@RequestParam String experimentName) {
        return experimentConfigurationService.findByExperimentName(experimentName);
    }

    @PostMapping("/findOrCreate")
    ExperimentConfiguration findOrCreate(
            @RequestParam String experimentIdHex,
            @RequestParam(required = false) String configurationName,
            @RequestBody(required = false) Map<String, Object> parameters
    ) throws InvalidExperimentIdException {
        val experimentId = IdDispatcher.getExperimentId(experimentIdHex);
        val parametersMap = Optional.ofNullable(parameters).orElse(Map.of());
        val now = Instant.now();
        val experimentConfiguration =
                new ExperimentConfiguration(ObjectId.get(), experimentId, configurationName, parametersMap, now);
        return experimentConfigurationService.findOrCreateConfiguration(experimentConfiguration);
    }

    @PutMapping("/update")
    ExperimentConfiguration update(
            @RequestParam String configurationIdHex,
            @RequestParam(required = false) String configurationName,
            @RequestParam(defaultValue = "false") boolean overrideParams,
            @RequestBody(required = false) Map<String, Object> parameters
    ) throws InvalidConfigurationIdException {
        val configurationId = IdDispatcher.getConfigurationId((configurationIdHex));

        return experimentConfigurationService
                .update(configurationId, configurationName, parameters, overrideParams);
    }

    @DeleteMapping("/deleteById")
    void deleteConfiguration(@RequestParam String configurationIdHex) throws InvalidConfigurationIdException {
        val configurationId = IdDispatcher.getConfigurationId(configurationIdHex);

        experimentConfigurationService.deleteById(configurationId);
    }

}
