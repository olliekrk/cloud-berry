package com.cloudberry.cloudberry.rest.api;

import com.cloudberry.cloudberry.properties.ApiPropertiesService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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

}
