package com.cloudberry.cloudberry.properties;

import com.cloudberry.cloudberry.properties.model.ApiProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ApiPropertiesService {

    private final ApiPropertyRepository apiPropertyRepository;

    public String getOrDefault(String key, String defaultValue) {
        return apiPropertyRepository.findById(key)
                .map(ApiProperty::getValue)
                .orElse(defaultValue);
    }

    public void set(String key, String value) {
        apiPropertyRepository.save(new ApiProperty(key, value));
    }

    public void reset(String key) {
        apiPropertyRepository.deleteById(key);
    }

}
