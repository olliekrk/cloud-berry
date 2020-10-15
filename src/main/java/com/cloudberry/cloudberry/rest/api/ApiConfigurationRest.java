package com.cloudberry.cloudberry.rest.api;

import com.cloudberry.cloudberry.properties.ApiPropertiesService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("apiConfiguration")
@RequiredArgsConstructor
public class ApiConfigurationRest {

    private final ApiPropertiesService apiPropertiesService;

    @GetMapping("property/{key}")
    public String getProperty(@PathVariable String key) {
        return apiPropertiesService.getOrDefault(key, null);
    }

    @PutMapping("property/{key}")
    public void setProperty(@PathVariable String key, @RequestBody String value) {
        apiPropertiesService.set(key, value);
    }

    @DeleteMapping("property/{key}")
    public void deleteProperty(@PathVariable String key) {
        apiPropertiesService.reset(key);
    }

}
