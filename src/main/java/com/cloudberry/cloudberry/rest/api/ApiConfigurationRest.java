package com.cloudberry.cloudberry.rest.api;

import com.cloudberry.cloudberry.db.influx.service.InfluxPropertiesService;
import com.cloudberry.cloudberry.properties.ApiPropertiesService;
import com.cloudberry.cloudberry.properties.model.ApiProperty;
import com.cloudberry.cloudberry.properties.model.ApiPropertyId;
import com.cloudberry.cloudberry.properties.model.InfluxPropertyId;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("apiConfiguration")
@RequiredArgsConstructor
public class ApiConfigurationRest {

    private final ApiPropertiesService apiPropertiesService;
    private final InfluxPropertiesService influxPropertiesService;

    @GetMapping("property/{id}")
    public String getProperty(@PathVariable String id) {
        return apiPropertiesService.getOrDefault(new ApiPropertyId(id), null);
    }

    @GetMapping("allProperties")
    public List<ApiProperty> getAllProperties() {
        return apiPropertiesService.getAll();
    }

    @PutMapping("property/{id}")
    public void setProperty(@PathVariable String id, @RequestBody String value) {
        InfluxPropertyId.byId(id).ifPresentOrElse(
                influxProperty -> influxPropertiesService.setProperty(influxProperty, value),
                () -> apiPropertiesService.set(new ApiPropertyId(id), value)
        );
    }

    @DeleteMapping("property/{id}")
    public void deleteProperty(@PathVariable String id) {
        apiPropertiesService.reset(new ApiPropertyId(id));
    }

}
