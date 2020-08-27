package com.cloudberry.cloudberry.rest.api.metadata;

import com.cloudberry.cloudberry.db.mongo.data.metadata.ExperimentConfiguration;
import com.cloudberry.cloudberry.db.mongo.service.ConfigurationService;
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
@RequestMapping(value = "/metadata/configuration", consumes = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ConfigurationCrudRest {
    private final ConfigurationService configurationService;

    @GetMapping(value = "/all")
    List<ExperimentConfiguration> getAll() {
        return configurationService.findAll();
    }

    @GetMapping(value = "/byConfigurationFileName")
    List<ExperimentConfiguration> getAll(@RequestParam String configurationFileName) {
        return configurationService.findAllForConfigurationFileName(configurationFileName);
    }

    @GetMapping(value = "/byExperimentName")
    List<ExperimentConfiguration> getAllConfigurationForExperiment(@RequestParam String experimentName) {
        return configurationService.findAllForExperimentName(experimentName);
    }

    @PostMapping("/getOrCreate")
    ExperimentConfiguration getOrCreate(@RequestParam String experimentIdHex,
                                        @RequestParam(required = false) String configurationFileName,
                                        @RequestBody(required = false) Map<String, Object> parameters
    ) throws InvalidExperimentIdException {
        val experimentId = RestParametersUtil.getValidId(experimentIdHex)
                .orElseThrow(() -> new InvalidExperimentIdException(List.of(experimentIdHex)));
        val experimentParameters = Optional.ofNullable(parameters).orElse(Map.of());
        val now = Instant.now();
        val experimentConfiguration = new ExperimentConfiguration(experimentId, configurationFileName, experimentParameters, now);
        return configurationService.getOrCreateConfiguration(experimentConfiguration).block();
    }

}
