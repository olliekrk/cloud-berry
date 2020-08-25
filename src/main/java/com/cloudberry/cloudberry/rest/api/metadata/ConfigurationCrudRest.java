package com.cloudberry.cloudberry.rest.api.metadata;

import com.cloudberry.cloudberry.db.mongo.data.metadata.ExperimentConfiguration;
import com.cloudberry.cloudberry.db.mongo.service.ConfigurationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
}
