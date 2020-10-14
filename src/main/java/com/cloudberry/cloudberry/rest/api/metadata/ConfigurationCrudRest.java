package com.cloudberry.cloudberry.rest.api.metadata;

import com.cloudberry.cloudberry.db.mongo.data.metadata.ExperimentConfiguration;
import com.cloudberry.cloudberry.db.mongo.service.configuration.ExperimentConfigurationService;
import com.cloudberry.cloudberry.rest.exceptions.invalid.id.InvalidConfigurationIdException;
import com.cloudberry.cloudberry.rest.exceptions.invalid.id.InvalidExperimentIdException;
import com.cloudberry.cloudberry.rest.util.IdDispatcher;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

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
    List<ExperimentConfiguration> getAll() {
        return experimentConfigurationService.findAll();
    }

    @GetMapping(value = "/byConfigurationFileName")
    List<ExperimentConfiguration> getByConfigurationFileName(@RequestParam String configurationFileName) {
        return experimentConfigurationService.findAllForConfigurationFileName(configurationFileName);
    }

    @GetMapping(value = "/byExperimentName")
    List<ExperimentConfiguration> getAllConfigurationForExperiment(@RequestParam String experimentName) {
        return experimentConfigurationService.findAllForExperimentName(experimentName);
    }

    @PostMapping("/getOrCreate")
    ExperimentConfiguration getOrCreate(@RequestParam String experimentIdHex,
                                        @RequestParam(required = false) String configurationFileName,
                                        @RequestBody(required = false) Map<String, Object> parameters
    ) throws InvalidExperimentIdException {
        val experimentId = IdDispatcher.getExperimentId(experimentIdHex);
        val experimentParameters = Optional.ofNullable(parameters).orElse(Map.of());
        val now = Instant.now();
        val experimentConfiguration =
                new ExperimentConfiguration(experimentId, configurationFileName, experimentParameters, now);
        return experimentConfigurationService.getOrCreateConfiguration(experimentConfiguration).block();
    }

    @PutMapping("/update")
    ExperimentConfiguration update(@RequestParam String configurationIdHex,
                                   @RequestParam(required = false) String configurationFileName,
                                   @RequestParam(defaultValue = "false") boolean overrideParams,
                                   @RequestBody(required = false) Map<String, Object> parameters
    ) throws InvalidConfigurationIdException {
        val configurationId = IdDispatcher.getConfigurationId((configurationIdHex));

        return experimentConfigurationService
                .update(configurationId, configurationFileName, parameters, overrideParams);
    }

    @DeleteMapping("/deleteById")
    void deleteConfiguration(@RequestParam String configurationIdHex) throws InvalidConfigurationIdException {
        val configurationId = IdDispatcher.getConfigurationId(configurationIdHex);

        experimentConfigurationService.deleteById(configurationId);
    }

}
